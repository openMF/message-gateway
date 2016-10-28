package org.apache.messagegateway.sms.providers.impl.infobip;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Random;

import org.apache.messagegateway.exception.MessageGatewayException;
import org.apache.messagegateway.sms.data.SmsShortMessage;
import org.apache.messagegateway.sms.domain.SMSBridge;
import org.apache.messagegateway.sms.domain.SMSMessage;
import org.apache.messagegateway.sms.providers.SMSProvider;
import org.apache.messagegateway.sms.repository.SmsOutboundMessageRepository;
import org.apache.messagegateway.sms.util.Gsm0338;
import org.apache.messagegateway.sms.util.SmsMessageStatusType;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GSMSpecificFeature;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.MessageMode;
import org.jsmpp.bean.MessageType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.Session;
import org.jsmpp.session.SessionStateListener;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.jsmpp.util.StringParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service(value = "InfoBipSMS")
public class InfoBipMessageProvider implements SMSProvider {

	private static final Logger logger = LoggerFactory.getLogger(InfoBipMessageProvider.class);
	private long reconnectInterval = 10000L; // 10 seconds
	private SMPPSession session = null;
	public Boolean isConnected = false;
	public Boolean isReconnecting = false;
	public SmsGatewayConfiguration smsGatewayConfiguration;
	public Boolean reconnect = true;
	private final SmsOutboundMessageRepository smsOutboundMessageRepository;

	// represents the max size of a fragment of a concatenated short message
	public static final Integer SHORT_MESSAGE_FRAGMENT_MAX_SIZE = 140;

	private static final int MAX_SINGLE_MSG_SEGMENT_SIZE_UCS2 = 70;
	private static final int MAX_SINGLE_MSG_SEGMENT_SIZE_7BIT = 160;

	@Autowired
	public InfoBipMessageProvider(final SmsOutboundMessageRepository smsOutboundMessageRepository) {
		this.smsOutboundMessageRepository = smsOutboundMessageRepository;
		// get an instance of the SmsGatewayConfiguration class

	}

	@Override
	public void sendMessage(SMSBridge smsBridgeConfig, SMSMessage message) throws MessageGatewayException {
		//Create the sessions keep it active based on tenant and provider id
		//This approach will take lot of time to unbind and create the sessions
		unbindAndCloseSession() ;
		smsGatewayConfiguration = new SmsGatewayConfiguration(smsBridgeConfig.getBridgeConfigurations());
		connectAndBindSession() ;
		SmsGatewayMessage smsGatewayMessage = new SmsGatewayMessage(message.getId(), message.getExternalId(),
				message.getSourceAddress(), message.getMobileNumber(), message.getMessage());
		smsGatewayMessage = this.submitShortMessage(smsGatewayMessage);
		message.setSubmittedOnDate(new Date());
		if (!StringUtils.isEmpty(smsGatewayMessage.getExternalId())) {
			message.setExternalId(smsGatewayMessage.getExternalId());
			message.setDeliveryStatus(SmsMessageStatusType.SENT.getValue());
		} else {
			message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
		}
		smsOutboundMessageRepository.save(message);
	}

	/**
	 * @return currently active SMPPSession object
	 **/
	public SMPPSession getSession() {
		return session;
	}

	/**
	 * @return the system mode (production/development)
	 **/
	public final Boolean developmentMode() {
		return smsGatewayConfiguration.getDevelopmentMode();
	}

	/**
	 * @return identifier of the system requesting a bind to the SMSC.
	 **/
	public final String systemId() {
		String systemId = SMPPServerSimulatorConfiguration.SMS_GATEWAY_SYSTEM_ID.getValue().toString();

		if (!developmentMode()) {
			systemId = smsGatewayConfiguration.getSystemId();
		}

		return systemId;
	}

	/**
	 * @return SMS gateway host name
	 **/
	public final String host() {
		String host = SMPPServerSimulatorConfiguration.SMS_GATEWAY_HOSTNAME.getValue().toString();

		if (!developmentMode()) {
			host = smsGatewayConfiguration.getHostname();
		}

		return host;
	}

	/**
	 * @return SMPP connection port number
	 **/
	public final Integer port() {
		Integer port = Integer.parseInt(SMPPServerSimulatorConfiguration.SMS_GATEWAY_PORT.getValue().toString());

		if (!developmentMode()) {
			port = smsGatewayConfiguration.getPortNumber();
		}

		return port;
	}

	/**
	 * @return SMPP connection password
	 **/
	public final String password() {
		String password = SMPPServerSimulatorConfiguration.SMS_GATEWAY_PASSWORD.getValue().toString();

		if (!developmentMode()) {
			password = smsGatewayConfiguration.getPassword();
		}

		return password;
	}

	/**
	 * @return bind type used for the connection to the SMS gateway
	 **/
	public final BindType bindType() {
		return BindType.BIND_TRX;
	}

	/**
	 * @return type of system requesting the bind
	 **/
	public final String systemType() {
		String systemType = null;

		if (developmentMode()) {
			systemType = "cp";
		}

		return systemType;
	}

	/**
	 * @return Type of Number for use in routing Delivery Receipts
	 **/
	public final TypeOfNumber addrTon() {
		return TypeOfNumber.UNKNOWN;
	}

	/**
	 * @return Numbering Plan Identity for use in routing Delivery Receipts.
	 **/
	public final NumberingPlanIndicator addrNpi() {
		return NumberingPlanIndicator.UNKNOWN;
	}

	/**
	 * @return Address range for use in routing short messages and Delivery
	 *         Receipts to an ESME.
	 **/
	public final String addressRange() {
		return null;
	}

	/**
	 * @return SMS Application service associated with the message
	 **/
	public final String serviceType() {
		return "CMT";
	}

	/**
	 * @return type of number (TON) to be used in the SME (Short Message Entity)
	 *         originator address parameters
	 **/
	public final TypeOfNumber sourceAddrTon() {
		return TypeOfNumber.ALPHANUMERIC;
	}

	/**
	 * @return NPI (numeric plan indicator) to be used in the SME (Short Message
	 *         Entity) originator address parameters
	 **/
	public final NumberingPlanIndicator sourceAddrNpi() {
		return NumberingPlanIndicator.UNKNOWN;
	}

	/**
	 * @return type of number (TON) to be used in the SME (Short Message Entity)
	 *         destination address parameters
	 **/
	public final TypeOfNumber destAddrTon() {
		return TypeOfNumber.INTERNATIONAL;
	}

	/**
	 * @return numeric plan indicator (NPI) to be used in the SME (Short Message
	 *         Entity) destination address parameters
	 **/
	public final NumberingPlanIndicator destAddrNpi() {
		return NumberingPlanIndicator.UNKNOWN;
	}

	/**
	 * @return a new instance of the ESMClass() class
	 **/
	public final ESMClass esmClass() {
		return new ESMClass();
	}

	/**
	 * @return GSM Protocol ID
	 **/
	public final byte protocolId() {
		return (byte) 0;
	}

	/**
	 * @return priority level to be assigned to the short message
	 **/
	public final byte priorityFlag() {
		return (byte) 0;
	}

	/**
	 * @return date and time (relative to GMT) at which delivery of the message
	 *         must be attempted
	 **/
	public final String scheduledDeliveryTime() {
		return null;
	}

	/**
	 * @return expiration time of this message specified as an absolute date and
	 *         time of expiry
	 **/
	public final String validityPeriod() {
		return null;
	}

	/**
	 * @return new instance of the RegisteredDelivery() class which will
	 *         indicating if the message is a registered short message and thus
	 *         if a Delivery Receipt is required upon the message attaining a
	 *         final state
	 **/
	public final RegisteredDelivery registeredDelivery() {
		return new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE);
	}

	/**
	 * @return indication if submitted message should replace an existing
	 *         message between the specified source and destination
	 **/
	public final byte replaceIfPresentFlag() {
		return (byte) 0;
	}

	/**
	 * @return GSM Data-Coding-Scheme
	 **/
	public final DataCoding dataCoding() {
		// ignore any IDE warnings
		return new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false);
	}

	/**
	 * @return default short message to send, by providing an index into the
	 *         table of Predefined Messages set up by the SMSC administrator.
	 **/
	public final byte smDefaultMsgId() {
		return (byte) 0;
	}

	/**
	 * create new SMPPSession object, connect and bind SMPP session
	 * 
	 * @return {@link SMPPSession} object
	 **/
	public final SMPPSession connectAndBindSession() {
		session = new SMPPSession();

		try {
			session.connectAndBind(host(), port(), new BindParameter(bindType(), systemId(), password(), systemType(),
					addrTon(), addrNpi(), addressRange()));

			session.addSessionStateListener(new SessionStateListenerImpl());
			session.setMessageReceiverListener(new MessageReceiverListenerImpl());
		}

		catch (IOException e) {
			// log error
			logger.error("Failed to connect and bind to host");
		}

		// isConnect is set to true if the last reading valid PDU from remote
		// host is greater than 0, else false
		isConnected = (session.getLastActivityTimestamp() > 0);

		return session;
	}

	/**
	 * Unbind and close open SMPP session
	 * 
	 * @return None
	 **/
	public final void unbindAndCloseSession() {
		session.unbindAndClose();
	}

	/**
	 * Send the SMS message to the SMS gateway
	 * 
	 * @param smsGatewayMessage
	 *            SmsGatewayMessage object
	 * 
	 * @return {@link SmsGatewayMessage} object
	 **/
	public SmsGatewayMessage submitShortMessage(SmsGatewayMessage smsGatewayMessage) {
		String message = smsGatewayMessage.getMessage();

		if (message != null && message.length() > StringParameter.SHORT_MESSAGE.getMax()) {
			smsGatewayMessage = this.submitSegmentedShortMessages(smsGatewayMessage);
		}

		else if (message != null) {
			// create a new SmsShortMessage object
			final SmsShortMessage smsShortMessage = SmsShortMessage.newSmsShortMessage(smsGatewayMessage.getId(),
					this.serviceType(), this.sourceAddrTon(), this.sourceAddrNpi(),
					smsGatewayMessage.getSourceAddress(), this.destAddrTon(), this.destAddrNpi(),
					smsGatewayMessage.getMobileNumber(), this.esmClass(), this.protocolId(), this.priorityFlag(),
					this.scheduledDeliveryTime(), this.validityPeriod(), this.registeredDelivery(),
					this.replaceIfPresentFlag(), this.dataCoding(), this.smDefaultMsgId(), message.getBytes(), 1, 1);

			// send short message to SMSC (short message service center)
			smsGatewayMessage = this.submitShortMessage(smsShortMessage);
		}

		return smsGatewayMessage;
	}

	/**
	 * Send SMS message to the SMS gateway
	 * 
	 * @param smsShortMessage
	 * @return {@link SmsGatewayMessage} object
	 */
	public SmsGatewayMessage submitShortMessage(final SmsShortMessage smsShortMessage) {
		String messageId = "";

		try {
			messageId = session.submitShortMessage(smsShortMessage.getServiceType(),
					smsShortMessage.getSourceAddressTypeofNumber(),
					smsShortMessage.getSourceAddressNumberingPlanIndicator(), smsShortMessage.getSourceAddress(),
					smsShortMessage.getDestinationAddressTypeOfNumber(),
					smsShortMessage.getDestinationAddressNumberingPlanIndicator(),
					smsShortMessage.getDestinationAddress(), smsShortMessage.getEsmClass(),
					smsShortMessage.getProtocolId(), smsShortMessage.getPriorityFlag(),
					smsShortMessage.getScheduleDeliveryTime(), smsShortMessage.getValidityPeriod(),
					smsShortMessage.getRegisteredDelivery(), smsShortMessage.getReplaceIfPresentFlag(),
					smsShortMessage.getDataCoding(), smsShortMessage.getDefaultMessageId(),
					smsShortMessage.getShortMessage().getBytes());

			logger.info("Message segment " + smsShortMessage.getMessageSegmentNumber() + " out of "
					+ smsShortMessage.getTotalNumberOfMessageSegments() + " segments sent to "
					+ smsShortMessage.getDestinationAddress() + ", SMS gateway message ID is " + messageId);
		}

		catch (PDUException e) {
			// Invalid PDU parameter - mostly resulting from failed validation
			logger.error("Invalid PDU parameter", e);
		}

		catch (ResponseTimeoutException e) {
			// Response timeout
			logger.error("Response timeout");
		}

		catch (InvalidResponseException e) {
			// Invalid response
			logger.error("Receive invalid response");
		}

		catch (NegativeResponseException e) {
			// Receiving negative response (non-zero command_status)
			logger.error("Receive negative response");
		}

		catch (IOException e) {
			logger.error("IO error occur", e);
		}

		return new SmsGatewayMessage(smsShortMessage.getMessageId(), messageId, smsShortMessage.getSourceAddress(),
				smsShortMessage.getDestinationAddress(), smsShortMessage.getShortMessage());
	}

	/**
	 * Send segmented SMS messages to the SMS gateway
	 * 
	 * @param smsGatewayMessage
	 *            SmsGatewayMessage object
	 * 
	 * @return {@link SmsGatewayMessage} object
	 **/
	public SmsGatewayMessage submitSegmentedShortMessages(SmsGatewayMessage smsGatewayMessage) {
		String message = smsGatewayMessage.getMessage();

		Alphabet alphabet = null;
		int maximumSingleMessageSize = 0;
		byte[] originalMessageBytes = null;

		if (message != null && message.length() > StringParameter.SHORT_MESSAGE.getMax()) {
			try {
				if (Gsm0338.isEncodeableInGsm0338(message)) {
					originalMessageBytes = message.getBytes();
					alphabet = Alphabet.ALPHA_DEFAULT;
					maximumSingleMessageSize = MAX_SINGLE_MSG_SEGMENT_SIZE_7BIT;
				}

				else {
					originalMessageBytes = message.getBytes("UTF-16BE");
					alphabet = Alphabet.ALPHA_UCS2;
					maximumSingleMessageSize = MAX_SINGLE_MSG_SEGMENT_SIZE_UCS2;
				}

				// check if message needs splitting and set required sending
				// parameters
				byte[][] segmentedMessagesBytes = null;
				ESMClass esmClass = null;

				if (message.length() > maximumSingleMessageSize) {
					byte[] referenceNumber = new byte[1];
					new Random().nextBytes(referenceNumber);

					segmentedMessagesBytes = createConcatenatedBinaryShortMessages(originalMessageBytes,
							referenceNumber[0]);

					// set UDHI so PDU will decode the header
					esmClass = new ESMClass(MessageMode.DEFAULT, MessageType.DEFAULT, GSMSpecificFeature.UDHI);
				}

				else {
					segmentedMessagesBytes = new byte[][] { originalMessageBytes };
					esmClass = new ESMClass();
				}

				// submit all messages
				for (int i = 0; i < segmentedMessagesBytes.length; i++) {
					int segmentNumber = i + 1;

					// create a new SmsShortMessage object
					final SmsShortMessage smsShortMessage = SmsShortMessage.newSmsShortMessage(
							smsGatewayMessage.getId(), this.serviceType(), this.sourceAddrTon(), this.sourceAddrNpi(),
							smsGatewayMessage.getSourceAddress(), this.destAddrTon(), this.destAddrNpi(),
							smsGatewayMessage.getMobileNumber(), esmClass, this.protocolId(), this.priorityFlag(),
							this.scheduledDeliveryTime(), this.validityPeriod(), this.registeredDelivery(),
							this.replaceIfPresentFlag(), new GeneralDataCoding(alphabet, esmClass),
							this.smDefaultMsgId(), segmentedMessagesBytes[i], segmentNumber,
							segmentedMessagesBytes.length);

					// send short message to SMSC (short message service center)
					smsGatewayMessage = this.submitShortMessage(smsShortMessage);
				}
			}

			catch (IOException e) {
				logger.error("IO error occur", e);
			}
		}

		return smsGatewayMessage;
	}

	/**
	 * Reconnect session after specified interval.
	 * 
	 * @param timeInMillis
	 *            is the interval.
	 * @return None
	 */
	public void reconnectAndBindSession() {
		if (!isReconnecting && reconnect) {
			new Thread() {
				@Override
				public void run() {
					logger.info("Schedule reconnect after " + reconnectInterval + " millis");

					try {
						Thread.sleep(reconnectInterval);
					}

					catch (InterruptedException e) {
					}

					int attempt = 0;

					while (session == null || session.getSessionState().equals(SessionState.CLOSED)) {
						// it is reconnecting
						isReconnecting = true;

						logger.info("Reconnecting attempt #" + (++attempt) + "...");

						// create a new connection and bind the session
						session = connectAndBindSession();

						// wait for #reconnectInterval
						try {
							Thread.sleep(reconnectInterval);
						}

						catch (InterruptedException ee) {
						}
					}

					isReconnecting = false;
				}
			}.start();
		}
	}

	/**
	 * This class will receive the notification from {@link SMPPSession} for the
	 * state changes. It will schedule to re-initialize session.
	 **/
	private class SessionStateListenerImpl implements SessionStateListener {

		@Override
		public void onStateChange(SessionState arg0, SessionState newState, Session arg2) {
			if (newState.equals(SessionState.CLOSED)) {
				logger.info("Session closed");
				reconnectAndBindSession();
			}
		}
	}

	/**
	 * This listener will listen to every incoming short message, recognized by
	 * deliver_sm command. It will update the smsGatewayDeliveryReports list if
	 * there are any new delivery reports
	 **/
	private class MessageReceiverListenerImpl implements MessageReceiverListener {

		@Override
		public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
			if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {

				try {
					DeliveryReceipt deliveryReceipt = deliverSm.getShortMessageAsDeliveryReceipt();
					// retrieving the messageId in production is easy as getting
					// the id of the "DeliveryReceipt" object
					String messageId = deliveryReceipt.getId();

					// in development mode (a simulator gateway is used),
					// retrieving the message ID is different
					if (developmentMode()) {
						messageId = Long.toString(Long.parseLong(deliveryReceipt.getId()) & 0xffffffff, 16);
					}

					SmsMessageStatusType messageStatus = null;

					switch (deliveryReceipt.getFinalStatus()) {
					case DELIVRD:
						messageStatus = SmsMessageStatusType.DELIVERED;
						break;

					case REJECTD:
					case EXPIRED:
					case UNDELIV:
						// rejected, expired and undelivered are grouped as
						// failed
						messageStatus = SmsMessageStatusType.FAILED;
						break;

					default:
						// in all other cases the status is invalid
						messageStatus = SmsMessageStatusType.INVALID;
						break;
					}

					// create a new SmsGatewayDeliveryReport object with data
					// received from the SMS gateway
					SmsGatewayDeliveryReport smsGatewayDeliveryReport = new SmsGatewayDeliveryReport(messageId,
							deliveryReceipt.getSubmitDate(), deliveryReceipt.getDoneDate(), messageStatus);

					// update SmsGatewayDeliveryReport entity delivery status
					// and date
					processDeliveryReport(smsGatewayDeliveryReport);

					// log success message
					logger.info("Receiving delivery report for message '" + messageId + "' : "
							+ smsGatewayDeliveryReport.toString());
				}

				catch (InvalidDeliveryReceiptException e) {
					logger.error("Failed getting delivery report");
				}
			}

			else {
				// inbound SMS message, this is currently not enabled - should
				// never get in here
				logger.info("Receiving message : " + new String(deliverSm.getShortMessage()));
			}
		}

		@Override
		public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
			return null;
		}

		@Override
		public void onAcceptAlertNotification(AlertNotification alertNotification) {
		}
	}

	/**
	 * Process the delivery report. Update the delivery status and date of the
	 * SmsOutboundMessage entity
	 * 
	 * @param smsGatewayDeliveryReport
	 *            {@link SmsGatewayDeliveryReport} object
	 * @return None
	 **/
	public void processDeliveryReport(SmsGatewayDeliveryReport smsGatewayDeliveryReport) {

		if (smsGatewayDeliveryReport != null) {
			// get the SmsMessage object from the DB
			SMSMessage smsOutboundMessage = this.smsOutboundMessageRepository
					.findByExternalId(smsGatewayDeliveryReport.getExternalId());

			if (smsOutboundMessage != null) {
				// update the status of the SMS message
				smsOutboundMessage.setDeliveryStatus(smsGatewayDeliveryReport.getStatus().getValue());

				switch (smsGatewayDeliveryReport.getStatus()) {
				case DELIVERED:
					// update the delivery date of the SMS message
					smsOutboundMessage.setDeliveredOnDate(smsGatewayDeliveryReport.getDoneDate());
					break;

				default:
					// at the moment, do nothing
					break;
				}

				// save the "SmsOutboundMessage" entity
				this.smsOutboundMessageRepository.save(smsOutboundMessage);

				// log success message
				logger.info("SMS message with external ID '" + smsOutboundMessage.getExternalId()
						+ "' successfully updated. Status set to: "
						+ smsOutboundMessage.getDeliveryStatus().toString());
			}
		}
	}

	/**
	 * Creates multiple short messages (that include a user data header) by
	 * splitting the binaryShortMessage data into 134 byte parts. If the
	 * binaryShortMessage does not need to be concatenated (less than or equal
	 * to 140 bytes), this method will return NULL. <br>
	 * <br>
	 * WARNING: This method only works on binary short messages that use 8-bit
	 * bytes. Short messages using 7-bit data or packed 7-bit data will not be
	 * correctly handled by this method. <br>
	 * <br>
	 * For example, will take a byte message (in hex, 138 bytes long) <br>
	 * 01020304...85&lt;byte 134&gt;87888990 <br>
	 * <br>
	 * Would be split into 2 parts as follows (in hex, with user data
	 * header)<br>
	 * 050003CC020101020304...85&lt;byte 134&gt;<br>
	 * 050003CC020287888990<br>
	 * <br>
	 * http://en.wikipedia.org/wiki/Concatenated_SMS
	 *
	 * @param binaryShortMessage
	 *            The 8-bit binary short message to create the concatenated
	 *            short messages from.
	 * @param referenceNum
	 *            The CSMS reference number that will be used in the user data
	 *            header.
	 * @return NULL if the binaryShortMessage does not need concatenated or an
	 *         array of byte arrays representing each chunk (including UDH).
	 * @throws IllegalArgumentException
	 */
	private byte[][] createConcatenatedBinaryShortMessages(byte[] binaryShortMessage, byte referenceNum)
			throws IllegalArgumentException {
		if (binaryShortMessage == null) {
			return null;
		}
		// if the short message does not need to be concatenated
		if (binaryShortMessage.length <= 140) {
			return null;
		}

		// since the UDH will be 6 bytes, we'll split the data into chunks of
		// 134
		int numParts = (int) (binaryShortMessage.length / 134) + (binaryShortMessage.length % 134 != 0 ? 1 : 0);
		// logger.debug("numParts=" + numParts);

		byte[][] shortMessageParts = new byte[numParts][];

		for (int i = 0; i < numParts; i++) {
			// default this part length to max of 134
			int shortMessagePartLength = 134;
			if ((i + 1) == numParts) {
				// last part (only need to add remainder)
				shortMessagePartLength = binaryShortMessage.length - (i * 134);
			}

			// logger.debug("part " + i + " len: " + shortMessagePartLength);

			// part will be UDH (6 bytes) + length of part
			byte[] shortMessagePart = new byte[6 + shortMessagePartLength];
			// Field 1 (1 octet): Length of User Data Header, in this case 05.
			shortMessagePart[0] = (byte) 0x05;
			// Field 2 (1 octet): Information Element Identifier, equal to 00
			// (Concatenated short messages, 8-bit reference number)
			shortMessagePart[1] = (byte) 0x00;
			// Field 3 (1 octet): Length of the header, excluding the first two
			// fields; equal to 03
			shortMessagePart[2] = (byte) 0x03;
			// Field 4 (1 octet): 00-FF, CSMS reference number, must be same for
			// all the SMS parts in the CSMS
			shortMessagePart[3] = referenceNum;
			// Field 5 (1 octet): 00-FF, total number of parts. The value shall
			// remain constant for every short message which makes up the
			// concatenated short message. If the value is zero then the
			// receiving entity shall ignore the whole information element
			shortMessagePart[4] = (byte) numParts;
			// Field 6 (1 octet): 00-FF, this part's number in the sequence. The
			// value shall start at 1 and increment for every short message
			// which makes up the concatenated short message. If the value is
			// zero or greater than the value in Field 5 then the receiving
			// entity shall ignore the whole information element. [ETSI
			// Specification: GSM 03.40 Version 5.3.0: July 1996]
			shortMessagePart[5] = (byte) (i + 1);

			// copy this part's user data onto the end
			System.arraycopy(binaryShortMessage, (i * 134), shortMessagePart, 6, shortMessagePartLength);
			shortMessageParts[i] = shortMessagePart;
		}

		return shortMessageParts;
	}
	
	@SuppressWarnings("serial")
	class SessionKey implements Serializable {
		private final String tenantId ;
		private final String providerId ;
		
		public SessionKey(final String tenantId, final String providerId) {
			this.tenantId = tenantId ;
			this.providerId = providerId ;
		}
		
		@Override
		public boolean equals(Object obj) {
			boolean isEquals = false ;
			if(obj instanceof SessionKey) {
				SessionKey session = (SessionKey) obj ;
				isEquals = session.tenantId.equals(this.tenantId) && session.providerId.equals(this.providerId) ;
			}
			return isEquals ;
		}
	}
}
