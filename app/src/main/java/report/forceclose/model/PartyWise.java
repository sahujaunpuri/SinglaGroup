package report.forceclose.model;

import java.io.Serializable;

/**
 * Created by Rakesh on 01-Aug-17.
 */
public class PartyWise implements Serializable {
    private static final long serialVersionUID = 1L;

    String VType,PartyID,PartyName,SubPartyID,SubParty,RefName;
    int PendingItems,PendingQty,PendingAmt;

    public PartyWise(String VType,String PartyID, String PartyName, String SubPartyID, String SubParty,String RefName, int PendingItems, int PendingQty,int PendingAmt) {
        this.VType = VType;
        this.PartyID = PartyID;
        this.PartyName = PartyName;
        this.SubPartyID = SubPartyID;
        this.SubParty = SubParty;
        this.RefName = RefName;
        this.PendingItems = PendingItems;
        this.PendingQty = PendingQty;
        this.PendingAmt = PendingAmt;
    }

    public String getVType() {
        return VType;
    }
    public void setVType(String VType) {
        this.VType = VType;
    }

    public String getPartyID() {
        return PartyID;
    }
    public void setPartyID(String PartyID) {
        this.PartyID = PartyID;
    }

    public String getPartyName() {
        return PartyName;
    }
    public void setPartyName(String PartyName) {
        this.PartyName = PartyName;
    }
    
    public String getSubPartyID() {
        return SubPartyID;
    }
    public void setSubPartyID(String SubPartyID) {
        this.SubPartyID = SubPartyID;
    }

    public String getSubParty() {
        return SubParty;
    }
    public void setSubParty(String SubParty) {
        this.SubParty = SubParty;
    }

    public String getRefName() {
        return RefName;
    }
    public void setRefName(String RefName) {
        this.RefName = RefName;
    }

    public int getPendingItems() {
        return PendingItems;
    }
    public void setPendingItems(int PendingItems) {
        this.PendingItems = PendingItems;
    }

    public int getPendingQty() {
        return PendingQty;
    }
    public void setPendingQty(int PendingQty) {
        this.PendingQty = PendingQty;
    }

    public int getPendingAmt() {
        return PendingAmt;
    }
    public void setPendingAmt(int PendingAmt) {
        this.PendingAmt = PendingAmt;
    }
}
