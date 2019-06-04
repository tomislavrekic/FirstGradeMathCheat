package hr.ferit.tomislavrekic.firstgrademathcheat;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static hr.ferit.tomislavrekic.firstgrademathcheat.Constants.TAG;

public class WordEquationParser {
    private static final String[] OPERATIONS = {"PLUS", "MINUS", "EQUALS"};

    public static int parser(String input){
        if(input == null){
            return 0;
        }

        List<String> numbersList = new ArrayList<>();
        List<String> operationsList = new ArrayList<>();

        String[] numbers = input.split(OPERATIONS[0]+"|"+OPERATIONS[1]+"|"+OPERATIONS[2]);

        for (int j=0;j<numbers.length;j++){
            numbersList.add(numbers[j].trim());
        }

        StringBuilder builder = new StringBuilder();
        String tempString = input;
        for(int i=0; i<numbersList.size(); i++){

            String[] operations = tempString.split(numbersList.get(i), 2);
            builder.append(operations[0]);
            tempString = operations[1].trim();
        }
        builder.append(tempString);
        String[] operations = builder.toString().trim().split("\\s");

        for(int i=0; i<operations.length; i++){
            operationsList.add(operations[i]);
        }

        int result = 0;
        boolean nextAdd = true;
        boolean nextSub = false;
        boolean equalFound = false;
        for(int i=0;i<operationsList.size(); i++){
            if(nextAdd){
                result += Integer.valueOf(DigitNameToDigitConverter.replaceNumbers(numbersList.get(i)));
                nextAdd = false;
            }
            if(nextSub){
                result -= Integer.valueOf(DigitNameToDigitConverter.replaceNumbers(numbersList.get(i)));
                nextSub = false;
            }

            if(operationsList.get(i).equals(OPERATIONS[0])){
                nextAdd = true;
            }
            if(operationsList.get(i).equals(OPERATIONS[1])){
                nextSub = true;
            }
            if(operationsList.get(i).equals(OPERATIONS[2])){
                equalFound = true;
            }
        }

        if(equalFound){
            Log.d(TAG, "parserresult:"+ result+":");
        }

        return result;
    }
}
