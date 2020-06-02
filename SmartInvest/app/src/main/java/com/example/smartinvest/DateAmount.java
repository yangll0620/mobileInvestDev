package com.example.smartinvest;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class DateAmount implements Comparable<DateAmount>{
    Date date;
    float amount;

    public DateAmount(Date date, float amount)
    {
        this.date = date;
        this.amount = amount;
    }

    public DateAmount(Transaction trans)
    {// Construct dateAmount from trans
        this.date = trans.getTransDate();
        this.amount = trans.getTransAmount();
    }

    @Override
    public int compareTo(DateAmount dateAmount) {

        if (dateAmount.date.before(dateAmount.date)){
            return 1;
        }
        else if (dateAmount.date.after(this.date)) {
            return -1;
        }
        else {
            return 0;
        }
    }

    private static float SumXIRRAmout(List<DateAmount> dateAmountList, double XIRR){
        float sumA;
        long msPerDay = 24 * 60 * 60 * 1000; // milliseconds per day

        Date date_latest = dateAmountList.get(dateAmountList.size()-1).date;

        sumA = 0;
        for(int i=0; i< dateAmountList.size(); i++){

            long diffDays = (Math.abs(dateAmountList.get(i).date.getTime() - date_latest.getTime()))/msPerDay;

            sumA += dateAmountList.get(i).amount * Math.pow((1 + XIRR), diffDays/365.0);
        }

        return sumA;
    }

    public static double calcXIRR(List<DateAmount> dateAmountList, double initXIRR, double lr, double epsilon, int maxIterators){
        /*Returns the internal rate of return for a schedule of cash flows that is not necessarily periodic using iterative guess

            Args:
                dateAmountList:

                initXIRR: initialized XIRR

                lr: learning rate

                epsilon: the accuracy between the estimated sum and 0

                maxIterators: maximal iterators
        * */

        Collections.sort(dateAmountList);


        double XIRR = initXIRR;
        float sumA;


        int iteri = 1;
        sumA = SumXIRRAmout(dateAmountList, XIRR);

        if(sumA <10)
        {
            lr = 0.001;
        }

        // identify increase and decrease direction
        while(Math.abs(sumA) > epsilon & iteri < maxIterators){
            float sum1 = SumXIRRAmout(dateAmountList, XIRR - lr);
            float sum2 = SumXIRRAmout(dateAmountList, XIRR + lr);

            if(Math.abs(sum1) < Math.abs(sum2))
            {
                if(Math.abs(sum1) <  Math.abs(sumA))
                {
                    XIRR = XIRR - lr;
                    sumA = sum1;
                }
            }
            else {
                if (Math.abs(sum2) < Math.abs(sumA)) {
                    XIRR = XIRR + lr;
                    sumA = sum2;
                }
            }

            if(sumA <10)
            {// decrease learning rate
                lr = 0.001;
            }

            iteri++;
        }

        return XIRR;
    }

}
