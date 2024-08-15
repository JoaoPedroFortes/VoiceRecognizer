package com.fortes;

import com.fortes.recognizer.PTBRRecognizer;
import com.fortes.recorder.Recorder;
import com.fortes.util.ReconignerUtils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Main {

	public static final String AUDIO_FILE = "C:/audio/%s.wav";

	public static PTBRRecognizer recognizer = PTBRRecognizer.getInstance();

	public static String getAudio(String name) {
		return String.format(AUDIO_FILE, name);
	}

	public static void recognize(String path) {
		var result = recognizer.execute(path);
		System.out.println("Reconhecido: " + result.getText());
	}

	public static void main(String[] args) throws Exception {

		String fileName = ReconignerUtils.createRandomFileName();

		//Aguarda a criação do arquivo para executar o reconhecedor
		CompletableFuture<String> completableFuture = new CompletableFuture<>();

		//Grava o arquivo .wave
		Recorder.recordAudio(getAudio(fileName), Optional.of(completableFuture));

		var file = completableFuture.get();

		if (file != null) {
			//Executa a função para reconhecer a gravação
			recognize(file);
		}

		//Encerra o reconhecedor
		recognizer.close();
	}
}