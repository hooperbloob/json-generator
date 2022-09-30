package com.comcast.datafill.outputs;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.comcast.datafill.Status;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.Date;

/**
 *
 * @author bremed200
 */
public class S3Output extends AbstractOutput {

    private AmazonS3 client;
    private final String bucketRoot;
    private static ObjectMetadata metadataFor(long contentLength) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setLastModified(new Date());
        metadata.setContentType("application/json");
        return metadata;
    }

    private static AmazonS3URI asS3URI(Path path) { return new AmazonS3URI("s3://" + path.toString()); }

    public S3Output(PathBuilder pathBuilder, int theIndent, String theBucketRoot) {
        super(pathBuilder, theIndent);

        bucketRoot = theBucketRoot;
    }

    private AmazonS3 client() {
        if (client == null) {
            client = AmazonS3ClientBuilder.defaultClient();
        }
        return client;
    }

    private AmazonS3URI uriFrom(JSONObject json, Status status) {
        return asS3URI( outBuilder.pathFor(json, status.recordsCreated() + 1) );
    }

    private AmazonS3URI uriFrom(JSONArray array, Status status) {
        return asS3URI( outBuilder.pathFor(array, status.filesCreated() + 1) );
    }

    @Override
    public void consume(JSONObject json, Status status) {

        String jsonStr = json.toString(indentFactor);

        writeToS3( uriFrom(json, status), jsonStr, status);
        status.incrementRecordCount(1);
    }

    @Override
    public void consume(JSONArray array, Status status) {

        String jsonStr = array.toString(indentFactor);

        writeToS3( uriFrom(array, status), jsonStr, status);
        status.incrementRecordCount(array.length());
    }

    private void writeToS3(AmazonS3URI targetUri, String json, Status status) {

        byte[] bytes = json.getBytes();

        PutObjectRequest req = new PutObjectRequest(
                targetUri.getBucket(), targetUri.getKey(),
                new ByteArrayInputStream(bytes),
                metadataFor(bytes.length)
        );

        req.setCannedAcl(CannedAccessControlList.BucketOwnerFullControl);

        PutObjectResult result = client().putObject(req);
        // TODO check result
        status.incrementFileCount(1);
        status.addConsumed(json.length());
    }
}
