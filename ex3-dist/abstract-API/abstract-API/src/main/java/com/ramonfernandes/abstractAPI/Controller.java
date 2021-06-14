package com.ramonfernandes.abstractAPI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Controller
@RequestMapping("api")
public class Controller {

    private static final String FILENAME = "importante_file.txt";

    @PostMapping("write")
    public ResponseEntity<?> write(@RequestBody WriteRequest request) throws IOException {
        String newLine = "\n" + request.getValue();
        Files.write(Paths.get(FILENAME), newLine.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        return ResponseEntity.ok().build();
    }

    @GetMapping("read")
    public ResponseEntity<Integer> read() throws IOException {
        File myObj = new File(FILENAME);
        if (!myObj.exists()) {
            myObj.createNewFile();
            String str = "1000";
            BufferedWriter writer = new BufferedWriter(new FileWriter(myObj));
            writer.write(str);

            writer.close();
        }
        List<String> strings = readFromInputStream(new FileInputStream(myObj));
        return ResponseEntity.ok(Integer.valueOf(strings.get(strings.size() - 1)));
    }

    private List<String> readFromInputStream(InputStream inputStream)
            throws IOException {
        ArrayList<String> lines = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private StringBuilder readFromInputStreamReturnBuilder(InputStream inputStream)
            throws IOException {
        StringBuilder lines = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.append(line);
            }
        }
        return lines;
    }

}
