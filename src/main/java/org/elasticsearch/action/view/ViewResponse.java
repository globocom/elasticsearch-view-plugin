package org.elasticsearch.action.view;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.ElasticSearchParseException;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.index.get.GetResult;

public class ViewResponse extends ActionResponse implements
		Iterable<GetField> {

	private GetResult getResult;
	private String viewResult;

	ViewResponse() {
    }
	
	ViewResponse(GetResult getResult, String viewResult) {
        this.getResult = getResult;
        this.viewResult = viewResult;
    }

    /**
     * Does the document exists.
     */
    public boolean isExists() {
        return getResult.isExists();
    }

    /**
     * The index the document was fetched from.
     */
    public String getIndex() {
        return getResult.getIndex();
    }

    /**
     * The type of the document.
     */
    public String getType() {
        return getResult.getType();
    }

    /**
     * The id of the document.
     */
    public String getId() {
        return getResult.getId();
    }

    /**
     * The version of the doc.
     */
    public long getVersion() {
        return getResult.getVersion();
    }

    /**
     * The source of the document if exists.
     */
    public byte[] getSourceAsBytes() {
        return getResult.source();
    }

    /**
     * Returns the internal source bytes, as they are returned without munging (for example,
     * might still be compressed).
     */
    public BytesReference getSourceInternal() {
        return getResult.internalSourceRef();
    }

    /**
     * Returns bytes reference, also un compress the source if needed.
     */
    public BytesReference getSourceAsBytesRef() {
        return getResult.sourceRef();
    }

    /**
     * Is the source empty (not available) or not.
     */
    public boolean isSourceEmpty() {
        return getResult.isSourceEmpty();
    }

    /**
     * The source of the document (as a string).
     */
    public String getSourceAsString() {
        return getResult.sourceAsString();
    }

    /**
     * The source of the document (As a map).
     */
    public Map<String, Object> getSourceAsMap() throws ElasticSearchParseException {
        return getResult.sourceAsMap();
    }

    public Map<String, Object> getSource() {
        return getResult.getSource();
    }

    public Map<String, GetField> getFields() {
        return getResult.getFields();
    }

    public GetField getField(String name) {
        return getResult.field(name);
    }

    public Iterator<GetField> iterator() {
        return getResult.iterator();
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        getResult = GetResult.readGetResult(in);
        viewResult = in.readOptionalString();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        getResult.writeTo(out);
        out.writeOptionalString(getSourceTransformResult());
    }

	public String getSourceTransformResult() {
		return viewResult;
	}

	public void setSourceTransformResult(String viewResult) {
		this.viewResult = viewResult;
	}
}
