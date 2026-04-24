public class User {
    private String Username;
    private String Password;
    private double balance;
    private double total_income;
    private double total_spending;
    private double saving_goals;
    private double spending_limits;
    
    public User(String name, String password){
        this.Username= name;
        this.Password= password;
        this.balance=0;
        this.total_income=0;
        this.total_spending=0;
    }

    public void setbalance(double new_balance){
        this.balance=new_balance;
    }
    public void setincome(double new_income){
        this.total_income=new_income;
    }
    public String getname(){
        return Username;
    }
    public double getbalance(){
        return balance;
    }
    public double getincome(){
        return total_income;
    }
    public double getspending(){
        return total_spending;
    }
    public double getsaving(){
        return saving_goals;
    }
    public double getlimits(){
        return spending_limits;
    }
}
