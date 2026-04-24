package src.com.financialtracking;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction {
    private  double amount;
    private String remark;
    private boolean isIncome;
    private LocalDate eventDate;
    private LocalDateTime createdTimestamp;

    public Transaction(double Amount, String Remark){
        this.amount= Amount;
        this.remark= Remark;
        this.isIncome= true;
        this.eventDate= LocalDate.now();
        this.createdTimestamp= LocalDateTime.now();
    }

    public void isExpense(){
        this.isIncome=false;
    }
    
}
