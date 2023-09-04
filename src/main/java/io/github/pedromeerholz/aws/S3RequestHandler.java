package io.github.pedromeerholz.aws;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;


@Service
public class S3RequestHandler {
    private final S3Client S3_CLIENT;
    private final String BUCKET_NAME;

    public S3RequestHandler() {
        S3_CLIENT = DependencyFactory.s3Client();
        BUCKET_NAME = "aws-s3-bucket-java-sdk";
    }
    public ResponseEntity storageObjectInBucket(String fileName, String filePath) {
        try {
            PutObjectRequest putObject = PutObjectRequest.builder()
                    .bucket(this.BUCKET_NAME)
                    .key(fileName)
                    .build();

            if (!this.verifyIfFileNameExists(fileName)) {
                this.S3_CLIENT.putObject(putObject, RequestBody.fromFile(new File(filePath)));
                return new ResponseEntity("Arquivo armazenado com sucesso!", HttpStatus.CREATED);
            }
            return new ResponseEntity("Um arquivo com esse nome já existe. Favor escolher outro nome.", HttpStatus.NOT_ACCEPTABLE);
        } catch (S3Exception exception) {
            System.err.println(exception.getMessage());
            return new ResponseEntity("Erro interno", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean verifyIfFileNameExists(String fileName) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(this.BUCKET_NAME)
                    .key(fileName)
                    .build();
            HeadObjectResponse response = this.S3_CLIENT.headObject(request);
            if (response.toString().contains("404")) {
                return false;
            }
            return true;
        } catch (S3Exception exception) {
            System.err.println(exception.getMessage());
            return false;
        }
    }
}
