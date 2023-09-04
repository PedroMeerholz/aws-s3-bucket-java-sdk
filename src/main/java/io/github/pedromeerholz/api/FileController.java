package io.github.pedromeerholz.api;

import io.github.pedromeerholz.aws.S3RequestHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/file")
public class FileController {
    private final S3RequestHandler s3RequestHandler;

    public FileController(S3RequestHandler s3RequestHandler) {
        this.s3RequestHandler = s3RequestHandler;
    }

    @PostMapping(value = "/upload/{fileName}")
    public ResponseEntity updaloadFile(@PathVariable("fileName") String fileName, @RequestHeader("FilePath") String filePath) {
        return this.s3RequestHandler.storageObjectInBucket(fileName, filePath);
    }
}
