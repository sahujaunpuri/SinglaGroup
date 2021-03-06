package uploadimagesfiles.documentattachment.responsedatasets;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import uploadimagesfiles.documentattachment.datasets.DocumentSpinnerDataset;


/**
 * Created by Rakesh on 27-Feb-16.
 */
public class ResponseDocumentSpinnerDataset {

    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("Result")
    private List<DocumentSpinnerDataset> result;

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public List<DocumentSpinnerDataset> getResult() {
        return result;
    }
    public void setResult(List<DocumentSpinnerDataset> result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
