package com.fortes.recorder;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.sound.sampled.TargetDataLine;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.fortes.util.ReconignerUtils.createFile;

public class Recorder {

	// Especifica o formato de áudio esperado pelo vosk (16F, 16bits, mono)
	private static final AudioFormat FORMAT = new AudioFormat(16000.0F, 16, 1, true, false);

	/**
	 * Método que grava o áudio a partir do microfone e salva no arquivo especificado.
	 *
	 *
	 * @param filePath Caminho completo do arquivo onde o áudio será salvo
	 * @throws
	 */
	public static void recordAudio(String filePath, Optional<CompletableFuture<String>> optionalCompletableFuture) throws IOException {

		// Verifica se o sistema suporta o formato de áudio desejado
		if (!AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
			System.out.println("Microfone não suportado.");
			return;
		}

		// Configura o DataLine para captura de áudio
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMAT);
		if (!AudioSystem.isLineSupported(info)) {
			System.out.println("Linha de áudio não suportada.");
			return;
		}

		try {
			File outputFile = createFile(filePath);

			// Obtém a linha de captura
			TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(FORMAT);
			line.start();


			System.out.println("Gravando... Pressione Enter para parar.");

			// Cria um thread para gravar o áudio
			Thread recordingThread = new Thread(() -> {
				try (AudioInputStream audioStream = new AudioInputStream(line)) {

					AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, outputFile); // Escreve o arquivo no formato wave

					optionalCompletableFuture.ifPresent(stringCompletableFuture -> stringCompletableFuture.complete(outputFile.getAbsolutePath()));

				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});

			recordingThread.start();

			// Aguarda a entrada do usuário para parar a gravação
			System.in.read();

			line.stop();
			line.close();
			System.out.println("Gravação finalizada.");

		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	}
