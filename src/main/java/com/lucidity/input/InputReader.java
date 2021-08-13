package com.lucidity.input;

import com.lucidity.model.Destination;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface InputReader {
    Map<String, Destination> takeInput(InputStream inputStream) throws IOException;
}
