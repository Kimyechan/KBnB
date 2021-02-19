package com.buildup.kbnb.dto.host.Income;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeResponse {
    int yearlyIncome = 0;
    int Jan = 0;   int May = 0;   int Sep = 0;
    int Feb = 0;   int Jun = 0;   int Oct = 0;
    int Mar = 0;   int Jul = 0;   int Nov = 0;
    int Apr = 0;   int Agu = 0;   int Dec = 0;

    public void add(int income, int identifier) {
        switch (identifier) {
            case 1:  Jan += income;
                break;
            case 2:  Feb += income;
                break;
            case 3:  Mar += income;
                break;
            case 4:  Apr += income;
                break;
            case 5:  May += income;
                break;
            case 6:  Jun += income;
                break;
            case 7:  Jul += income;
                break;
            case 8:  Agu += income;
                break;
            case 9:  Sep += income;
                break;
            case 10: Oct += income;
                break;
            case 11: Nov += income;
                break;
            case 12: Dec += income;
                break;
            default:
                break;
        }
    }
    public int setYearlyIncome() {
        this.yearlyIncome = getJan() + getFeb() + getMar() + getApr() +
                getMay() + getJun() + getJul() + getAgu() +
                getSep() + getOct() + getNov() + getDec();
        return yearlyIncome;
    }
}
