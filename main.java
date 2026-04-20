import java.util.Scanner;

class financialtracking {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        double balance = 0;
        double total_spending = 0, total_income = 0;
        double saving_goal = 0, expense_limit = 0;
        double gas = 0, grocery = 0, food = 0, drinks = 0, hobbies = 0, Ex_others = 0;
        double allowance = 0, salary = 0, In_others = 0;

        while (true) {
            System.out.println("-----Financial Tracking System-----");
            System.out.println("1. Insert Income");
            System.out.println("2. Insert Expense");
            System.out.println("3. Set Saving Goals");
            System.out.println("4. Set Expense Limit");
            System.out.println("5. View Summary");
            System.out.println("6. Exit");
            System.out.print("Enter your option: ");
            option = scanner.nextInt();

            if (option == 6) {
                System.out.println("Exiting... Goodbye!");
                break;
            }

            if (option == 1) {
                System.out.print("Insert income amount: ");
                double income = scanner.nextDouble();
                total_income += income;
                balance += income;

                while (true) {
                    System.out.println("What is the income type?");
                    System.out.println("1.Allowance \n2.Salary \n3.Others");
                    System.out.print("Input your option: ");
                    int type_option = scanner.nextInt();
                    switch (type_option) {
                        case 1:
                            allowance += income;
                            break;
                        case 2:
                            salary += income;
                            break;
                        case 3:
                            In_others += income;
                            break;
                        default:
                            System.out.println("Invalid option, please try again.");
                            continue;
                    }
                    break;
                }
            } else if (option == 2) {
                System.out.print("Insert expense amount: ");
                double expense = scanner.nextDouble();
                total_spending += expense;
                balance -= expense;

                while (true) {
                    System.out.println("What is the expense type?");
                    System.out.println("1.Gas \n2.Grocery \n3.Food \n4.Drinks \n5.Hobbies \n6.Others");
                    System.out.print("Input your option: ");
                    int type_option = scanner.nextInt();
                    switch (type_option) {
                        case 1:
                            gas += expense;
                            break;
                        case 2:
                            grocery += expense;
                            break;
                        case 3:
                            food += expense;
                            break;
                        case 4:
                            drinks += expense;
                            break;
                        case 5:
                            hobbies += expense;
                            break;
                        case 6:
                            Ex_others += expense;
                            break;
                        default:
                            System.out.println("Invalid option, please try again.");
                            continue;
                    }
                    break;
                }
            } else if (option == 3) {
                System.out.print("Enter Your Saving Goals: ");
                saving_goal = scanner.nextDouble();
                System.out.printf("Current Balance is $%.2f\n", balance);
                if (saving_goal > 0) {
                    double percentage = (balance / saving_goal) * 100;
                    System.out.printf("You are %.2f%% toward your Saving Goal\n", percentage);
                } else {
                    System.out.println("Saving goal not set yet.");
                }
            } else if (option == 4) {
                System.out.print("Enter Your Expense Limit: ");
                expense_limit = scanner.nextDouble();
                System.out.printf("Current Balance is $%.2f\n", balance);
                System.out.printf("Current Total Expense is $%.2f\n", total_spending);
                if (expense_limit > 0) {
                    double percentage = (total_spending / expense_limit) * 100;
                    System.out.printf("You are %.2f%% of your Expense Limit.\n", percentage);
                } else {
                    System.out.println("Expense limit not set yet.");
                }
            } else if (option == 5) {
                System.out.println("----- Transaction Summary -----");
                System.out.printf("Current Balance:\t$%.2f\n", balance);
                System.out.printf("Total Income:\t\t$%.2f\n", total_income);
                System.out.printf("Total Expense:\t\t$%.2f\n", total_spending);

                if (expense_limit > 0) {
                    double limitPercentage = (total_spending / expense_limit) * 100;
                    System.out.printf("Expense Limit:\t\t$%.2f (%.2f%% reached)\n", expense_limit, limitPercentage);
                } else {
                    System.out.println("Expense Limit:\t\tNot set");
                }

                if (saving_goal > 0) {
                    double goalPercentage = (balance / saving_goal) * 100;
                    System.out.printf("Saving Goals:\t\t$%.2f (%.2f%% reached)\n", saving_goal, goalPercentage);
                } else {
                    System.out.println("Saving Goals:\t\tNot set");
                }

                System.out.println("------- Expense Summary -------");
                System.out.printf("\t1.Gas\t\t$%.2f\n", gas);
                System.out.printf("\t2.Grocery\t$%.2f\n", grocery);
                System.out.printf("\t3.Food\t\t$%.2f\n", food);
                System.out.printf("\t4.Drinks\t$%.2f\n", drinks);
                System.out.printf("\t5.Hobbies\t$%.2f\n", hobbies);
                System.out.printf("\t6.Others\t$%.2f\n", Ex_others);

                System.out.println("------- Income Summary --------");
                System.out.printf("\t1.Allowance\t$%.2f\n", allowance);
                System.out.printf("\t2.Salary\t$%.2f\n", salary);
                System.out.printf("\t3.Others\t$%.2f\n", In_others);
                System.out.println("------------------------------");
            } else {
                System.out.println("Invalid option, please choose 1-6.");
            }
        }
    }
}
