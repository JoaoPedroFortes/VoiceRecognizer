package com.fortes.recognizer;

import com.fortes.pojo.Result;

public interface IRecognizer {

	Result execute(String audioPath);

	Result getResult(String result);

	void close();
}
