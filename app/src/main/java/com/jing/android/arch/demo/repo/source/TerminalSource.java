package com.jing.android.arch.demo.repo.source;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

/**
 * @author JingTuo
 */
public class TerminalSource implements SingleOnSubscribe<String> {

    private String[] commands;

    public TerminalSource(String[] commands) {
        this.commands = commands;
    }

    @Override
    public void subscribe(@NonNull SingleEmitter<String> emitter) throws Throwable {
        ProcessBuilder builder = new ProcessBuilder(commands);
        Process process = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String temp;
        StringBuilder result = new StringBuilder();
        while (true) {
            temp = reader.readLine();
            if (temp == null) {
                break;
            } else {
                result.append(temp).append("\n");
            }
        }
        process.waitFor();
        emitter.onSuccess(result.toString());
    }
}
