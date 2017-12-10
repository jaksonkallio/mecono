package mecono;

import org.json.*;

/**
 *
 * @author jak
 */
public abstract class Parcel {

    public Parcel(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    /**
     * Which node originated this parcel, supposedly.
     *
     * @return RemoteNode Originator node object.
     */
    public abstract Node getOriginator() throws MissingParcelDetailsException;

    public void setPath(Path path) {
        this.path = path;
    }

    public Path getPath() throws MissingParcelDetailsException {
        return path;
    }

    /**
     * Gets the next node in the path.
     *
     * @return
     */
    public RemoteNode getNextNode() throws MissingParcelDetailsException {
        return null;
    }

    public JSONObject serialize() {
        return null;
    }

    public static Parcel unserialize(JSONObject json_parcel, SelfNode relative_self) throws BadProtocolException, UnknownResponsibilityException {
        Parcel received_parcel = null;
        Mailbox mailbox = relative_self.getMailbox();

        if (json_parcel.getString("destination").equals(relative_self.getAddress())) {
            switch (Protocol.parcel_type_codes[json_parcel.getInt("parcel_type")]) {
                case PING:
                    received_parcel = new PingParcel(mailbox, DestinationParcel.TransferDirection.INBOUND);
                    break;
                case PING_RESPONSE:
                    received_parcel = new PingResponseParcel(mailbox, DestinationParcel.TransferDirection.INBOUND);
                    break;
                case FIND:
                    received_parcel = new FindParcel(mailbox, DestinationParcel.TransferDirection.INBOUND);
                    break;
                case FIND_RESPONSE:
                    received_parcel = new FindResponseParcel(mailbox, DestinationParcel.TransferDirection.INBOUND);
                    break;
                case DATA:
                    received_parcel = new DataParcel(mailbox, DestinationParcel.TransferDirection.INBOUND);
                    break;
                case DATA_RECEIPT:
                    received_parcel = new DataReceiptParcel(mailbox, DestinationParcel.TransferDirection.INBOUND);
                    break;
                default:
                    received_parcel = new DestinationParcel(mailbox, DestinationParcel.TransferDirection.INBOUND);
            }
        }

        return received_parcel;
    }

    public static int getParcelTypeCode(ParcelType target) {
        for (int i = 0; i < Protocol.parcel_type_codes.length; i++) {
            if (Protocol.parcel_type_codes[i] == target) {
                return i;
            }
        }
        return -1;
    }

    protected final Mailbox mailbox;
    protected Node originator;
    protected Path path_history;
    protected Path path;
}
