/*
 * Copyright (C) 2020 Wigo Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.moara.nia.data.build.evaluator;

import org.moara.classification.binary.BinaryClassificationEvaluation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 하이라이팅 성능 평가
 *
 * @author wjrmffldrhrl
 */
public class HighlightEvaluator {

    private final Integer[] answerPositives;
    private final Integer[] answerNegatives;
    private Integer[] submitPositives;
    private Integer[] submitNegatives;
    private int sentenceLength;

    /**
     * 정답 데이터 초기화
     * @param line 하이라이팅 답안 정보가 담겨있는 문자열
     *             {@code HighlightExtractor}에 의해서 추출되는 정보를 주입하면 된다.
     *             ex) [물 복지 실현에는 총 125억원의 사업비가 투입된다.] : [10, 11]
     *
     */
    public HighlightEvaluator(String line) {
        String[] data = line.split("\\] \\: \\[");
        String sentence = data[0].substring(1);
        String[] highlightStr = data[1].substring(0, data[1].length() - 1).split(",");

        this.answerPositives = new Integer[highlightStr.length];
        this.answerNegatives = new Integer[sentence.length() - highlightStr.length + 1];

        int positiveIndex = 0;
        int negativeIndex = 0;
        this.sentenceLength = sentence.length();
        for (int i = 0; i < sentence.length() + 1; i++) {
            if (positiveIndex < this.answerPositives.length && Integer.parseInt(highlightStr[positiveIndex].trim()) == i) {
                this.answerPositives[positiveIndex++] = i;
            } else if (negativeIndex < this.answerNegatives.length) {
                this.answerNegatives[negativeIndex++] = i;
            }
        }
    }


    /**
     * 하이라이팅 결과 데이터 초기화
     * @param line 하이라이팅 정보가 담겨있는 문자열
     *      *             {@code HighlightExtractor}에 의해서 추출되는 정보를 주입하면 된다.
     *      *             ex) [물 복지 실현에는 총 125억원의 사업비가 투입된다.] : [10, 11]
     */
    public void initSubmit(String line) {
        String[] data = line.split("\\] \\: \\[");
        String sentence = data[0].substring(1);
        String[] highlightStr = data[1].substring(0, data[1].length() - 1).split(",");

        this.submitPositives = new Integer[highlightStr.length];
        this.submitNegatives = new Integer[sentence.length() - highlightStr.length + 1];

        int positiveIndex = 0;
        int negativeIndex = 0;
        this.sentenceLength = sentence.length();
        for (int i = 0; i < sentence.length() + 1; i++) {
            if (positiveIndex < this.submitPositives.length && Integer.parseInt(highlightStr[positiveIndex].trim()) == i) {
                this.submitPositives[positiveIndex++] = i;
            } else if (negativeIndex < this.submitNegatives.length) {
                this.submitNegatives[negativeIndex++] = i;
            }
        }
    }


    /**
     * 초기화한 정답 데이터와 입력한 모델의 하이라이팅 데이터를 통해 해당 모델의 성능을 측정한다.
     * 측정의 결과는 {@code BinaryClassificationEvaluation} 객체로 생성된다.
     * @return BinaryClassificationEvaluation
     */
    public BinaryClassificationEvaluation answerCheck() {
        List<Integer> answerPositives = new ArrayList(Arrays.asList(this.answerPositives));
        List<Integer> answerNegatives = new ArrayList(Arrays.asList(this.answerNegatives));
        List<Integer> submitPositives = new ArrayList(Arrays.asList(this.submitPositives));
        List<Integer> submitNegatives = new ArrayList(Arrays.asList(this.submitNegatives));


        List<Integer> truePositivePoints = answerPositives.stream()
                .filter(submitPositives::contains).collect(Collectors.toList());
        List<Integer> trueNegativePoints = answerNegatives.stream()
                .filter(submitNegatives::contains).collect(Collectors.toList());
        List<Integer> falseNegativePoints = answerPositives.stream()
                .filter(submitNegatives::contains).collect(Collectors.toList());
        List<Integer> falsePositivePoints = answerNegatives.stream()
                .filter(submitPositives::contains).collect(Collectors.toList());

        int truePositive = truePositivePoints.size();
        int trueNegative = trueNegativePoints.size();
        int falseNegative = falseNegativePoints.size();
        int falsePositive = falsePositivePoints.size();

        return new BinaryClassificationEvaluation(truePositive, trueNegative, falseNegative, falsePositive);
    }

    public static void main(String[] args) {

        int count = 0;
        double accuracy = 0.0;
        double geometricMean = 0.0;
        double recall = 0.0;
        double precision = 0.0;
        double f1Score = 0.0;

        double truePositive = 0;
        double trueNegative = 0;
        double falseNegative = 0;
        double falsePositive = 0;

        String answerSheetFileName = "answer";
        String submitSheetFileName = "submit";
        try (BufferedReader answerSheet = new BufferedReader(
                new InputStreamReader(new FileInputStream("./data/highlight/" + answerSheetFileName + ".txt"), StandardCharsets.UTF_8));
             BufferedReader submitSheet = new BufferedReader(
                     new InputStreamReader(new FileInputStream("./data/highlight/" + submitSheetFileName + ".txt"), StandardCharsets.UTF_8))

        ) {

            while (true) {
                String answerLine = answerSheet.readLine();
                String submitLine = submitSheet.readLine();
                if (answerLine == null || submitLine == null) {
                    break;
                }

                HighlightEvaluator highlightEvaluator = new HighlightEvaluator(answerLine);
                highlightEvaluator.initSubmit(submitLine);
                BinaryClassificationEvaluation evaluation = highlightEvaluator.answerCheck();

                if (evaluation.getAccuracy() != 1.0) {
                    System.out.println("submit (" + (count + 1) + ") : " + evaluation.toString());
                }

//                System.out.println("submit (" + (count + 1) + ") : " + evaluation.toString());
                count++;
//                truePositive += evaluation.getTruePositive();
//                trueNegative += evaluation.getTrueNegative();
//                falseNegative += evaluation.getFalseNegative();
//                falsePositive += evaluation.getFalsePositive();

                accuracy += evaluation.getAccuracy();
                geometricMean += evaluation.getGeometricMean();
                recall += evaluation.getSpecificity();
                precision += evaluation.getPrecision();
                f1Score += evaluation.getF1Score();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nData count : " + count);

        System.out.println("True Positive = " + truePositive);
        System.out.println("True Negative = " + trueNegative);
        System.out.println("False Negative = " + falseNegative);
        System.out.println("False Positive = " + falsePositive);

        System.out.println("Accuracy (정확도) \t\t: " + accuracy / count);
        System.out.println("Geometric Mean (균형 정확도)\t: " + geometricMean / count);
        System.out.println("Recall (재현율)\t\t\t: " + recall / count);
        System.out.println("Precision (정밀도)\t\t: " + precision / count);
        System.out.println("F1Score (F1 지수)\t\t: " + f1Score / count);


//        HighlightEvaluator highlightEvaluator = new HighlightEvaluator("[그러나 지난해는 120만명이 다녀가는 데 그쳤다.] : [0, 1, 2, 3]");
//        highlightEvaluator.initSubmit("[그러나 지난해는 120만명이 다녀가는 데 그쳤다.] : [0, 1, 2, 3, 21, 22]");
//        Evaluation evaluation = highlightEvaluator.answerCheck();
//        System.out.println(evaluation);
    }
}




