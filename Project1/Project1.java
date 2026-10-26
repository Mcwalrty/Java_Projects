import java.util.Scanner;
import java.util.Random;

class Project1 {
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String PURPLE = "\u001B[35m";
    public static final String LIGHT_BLUE = "\u001B[94m";

    public static String getStockColorString(int currentStock, int maxStock) {
        if (maxStock <= 0) maxStock = 1;
        double percentage = (double) currentStock / maxStock * 100.0;
        String stockStr = String.format("%2d", currentStock);
        
        if (percentage >= 80) {
            return GREEN + stockStr + RESET;
        } else if (percentage >= 50) {
            return YELLOW + stockStr + RESET;
        } else {
            return RED + stockStr + RESET;
        }
    }

    public static boolean isSurgeFruit(int currentDay, int fruitIndex, int[] surgeDays, int[][] surgeFruits) {
        for (int d = 0; d < 2; d++) {
            if (surgeDays[d] == currentDay) {
                if (surgeFruits[d][0] == fruitIndex || surgeFruits[d][1] == fruitIndex) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isCherrySweetDemandDay(int currentDay, int[] sweetDemandDays) {
        return (sweetDemandDays[0] != -1 && currentDay == sweetDemandDays[0]) || 
               (sweetDemandDays[1] != -1 && currentDay == sweetDemandDays[1]);
    }

    public static int advanceDay(int days, int pineapplesBoughtToday, int[] pineapplesBonusNextDayRef, 
                                 int[] watermelonAges, int[] blueberryAges, int[] playerInventory) {
        
        pineapplesBonusNextDayRef[0] = pineapplesBoughtToday * 45;

        if (watermelonAges[0] > 0) {
            System.out.println(RED + "Oh no! " + watermelonAges[0] + " Watermelon(s) spoiled after 3 days!" + RESET);
        }
        watermelonAges[0] = watermelonAges[1];
        watermelonAges[1] = watermelonAges[2];
        watermelonAges[2] = 0;
        playerInventory[4] = watermelonAges[0] + watermelonAges[1] + watermelonAges[2];

        for (int k = 5; k > 0; k--) {
            blueberryAges[k] = blueberryAges[k - 1];
        }
        blueberryAges[0] = 0;
        playerInventory[6] = blueberryAges[0] + blueberryAges[1] + blueberryAges[2] + 
                             blueberryAges[3] + blueberryAges[4] + blueberryAges[5];

        return days + 1;
    }

    public static void displayGameOver(int score, int finalMoney, Scanner scanner) throws InterruptedException {
        System.out.println("\n=== GAME OVER ===");
        System.out.println("------------------------------------------------");
        System.out.println("             PERFORMANCE SUMMARY                ");
        System.out.println("------------------------------------------------");
        System.out.println(" Days Completed     : 10/10");
        System.out.println(" Final Cash in Hand : $" + finalMoney);
        System.out.println(" Total Score (Spent): $" + score);
        System.out.println("------------------------------------------------");
        System.out.println("Thanks for playing!");
        Thread.sleep(2000);
        scanner.close();
    }

    public static void main(String[] args) throws InterruptedException {
        String[] fruits         = {"Apple",  "Banana", "Cherry", "Pineapple", "Watermelon", "Grape", "Blueberry", "Dragon Fruit", "Kiwi"};
        int[]    price          = {2,        5,        10,       50,          30,           15,      35,          80,             100};
        int[]    sellPrice      = {1,        2,        8,        10,          15,           8,       20,          30,             50};  // Cherry base sell set to 8
        int[]    stock          = {10,       10,       10,       5,           5,            20,      5,           2,              3};
        int[]    maxStock       = {10,       10,       10,       5,           5,            20,      5,           2,              3};
        int[]    playerInventory = {0,       0,        0,        0,           0,            0,       0,           0,              0};
        
        int[] watermelonAges = {0, 0, 0}; 
        int[] blueberryAges = {0, 0, 0, 0, 0, 0};

        int days = 1;
        int money = 5;
        boolean recievedDailyMoney = false;
        boolean newDay = true;
        int dragonFruitBought = 0;
        int pineapplesBoughtToday = 0;
        int[] pineapplesBonusNextDay = {0};
        int score = 0;
        int watermelonDailySellPrice = 15;

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        int saleDay = 3 + random.nextInt(3);
        int saleFruitIndex = 2 + random.nextInt(5); 

        int sweetDemandDay1 = random.nextBoolean() ? (3 + random.nextInt(3)) : -1; 
        int sweetDemandDay2 = random.nextBoolean() ? (7 + random.nextInt(3)) : -1; 
        int[] sweetDemandDays = {sweetDemandDay1, sweetDemandDay2};

        int surgeDay1 = 2 + random.nextInt(9); 
        int surgeDay2;
        do {
            surgeDay2 = 2 + random.nextInt(9);
        } while (surgeDay2 == surgeDay1 || Math.abs(surgeDay2 - surgeDay1) == 1);

        int[] surgeDays = {surgeDay1, surgeDay2};
        int[][] surgeFruits = new int[2][2];
        for (int d = 0; d < 2; d++) {
            surgeFruits[d][0] = random.nextInt(7);
            do {
                surgeFruits[d][1] = random.nextInt(7);
            } while (surgeFruits[d][1] == surgeFruits[d][0]);

            if (surgeDays[d] == saleDay) {
                if (surgeFruits[d][0] == saleFruitIndex) {
                    surgeFruits[d][0] = (saleFruitIndex + 1) % 7;
                } else if (surgeFruits[d][1] == saleFruitIndex) {
                    surgeFruits[d][1] = (saleFruitIndex + 1) % 7;
                }
            }
        }

        while (true) {
            int displayLimit = (days >= 6) ? fruits.length : 7; 

            if (newDay) {
                System.out.println(LIGHT_BLUE + "\n================================================");
                System.out.println("          WELCOME TO THE FRUIT STAND!            ");
                System.out.println("================================================" + RESET);
                System.out.println();
                System.out.println("Day: " + days + "/10");
                System.out.println();
                Thread.sleep(500);
                
                watermelonDailySellPrice = 15 + random.nextInt(36);
                
                if (days != 1 && !recievedDailyMoney) {
                    int dailyEarnings = (10 * days) + (dragonFruitBought * 20) + pineapplesBonusNextDay[0];
                    System.out.println("I recieved $" + dailyEarnings + " dollars for this day!\n");
                    money = dailyEarnings;
                    recievedDailyMoney = true;
                    pineapplesBonusNextDay[0] = 0;
                }

                if (watermelonAges[0] > 0) {
                    System.out.println(RED + "------------------------------------------------");
                    System.out.println("  [WARNING] You have " + watermelonAges[0] + " Watermelon(s) about to spoil!");
                    System.out.println("  Sell them today or they will perish!  ");
                    System.out.println("------------------------------------------------" + RESET + "\n");
                    Thread.sleep(1000);
                }
                
                if (isCherrySweetDemandDay(days, sweetDemandDays)) {
                    int cherryBuyPriceDouble = price[2] * 2;
                    int cherrySellPriceDouble = sellPrice[2] * 2;
                    System.out.println(PURPLE + "SWEET DEMAND! Cherries cost double ($" + cherryBuyPriceDouble + ") to buy and sell for double ($" + cherrySellPriceDouble + ") today!" + RESET + "\n");
                    Thread.sleep(1000);
                }

                if (days == saleDay) {
                    System.out.println(YELLOW + "FLASH SALE! Today only, " + fruits[saleFruitIndex] + "s are 50% off!" + RESET + "\n");
                    Thread.sleep(1000);
                }

                for (int d = 0; d < 2; d++) {
                    if (surgeDays[d] == days) {
                        System.out.println(YELLOW + "MARKET SURGE! " + fruits[surgeFruits[d][0]] + " and " + fruits[surgeFruits[d][1]] + " are in high demand today! Buy and sell prices are doubled, shop stock is halved!" + RESET + "\n");
                        Thread.sleep(1000);
                    }
                }

                if (days == 6) {
                    System.out.println("New items have entered the shop....\n");
                    Thread.sleep(1000);
                }
                
                if (days == 10) {
                    System.out.println("Today is the final day, let's make it count!");
                    Thread.sleep(1000);
                }
                
                newDay = false;
            }
            
            for (int i = 0; i < displayLimit; i++) {
                int displayPrice = price[i];
                boolean isSurge = isSurgeFruit(days, i, surgeDays, surgeFruits);
                boolean isSweetDemand = (i == 2 && isCherrySweetDemandDay(days, sweetDemandDays));
                
                if (isSweetDemand) {
                    displayPrice *= 2;
                } else if (days == saleDay && i == saleFruitIndex) {
                    displayPrice /= 2;
                } else if (isSurge) {
                    displayPrice *= 2;
                }
                
                int effectiveMaxStock = isSurge ? Math.max(1, maxStock[i] / 2) : maxStock[i];
                int effectiveStock = Math.min(stock[i], effectiveMaxStock);

                String coloredStock = getStockColorString(effectiveStock, effectiveMaxStock);
                String priceFormatted = String.format("$%3d", displayPrice);
                if (isSweetDemand) {
                    priceFormatted = PURPLE + priceFormatted + RESET;
                } else if (isSurge) {
                    priceFormatted = YELLOW + priceFormatted + RESET;
                }

                System.out.printf("%2d. %-15s | Price: %s | Stock: %s\n", (i + 1), fruits[i], priceFormatted, coloredStock);
            }
            
            Thread.sleep(1000);
            System.out.println("\nYour total money is: $" + money);
            System.out.println("What would you like to do?");
            Thread.sleep(500);
            System.out.println("1. Buy | 2. Sell | 3. Next Day | 4. Quit | 5. Check Inventory | 6. Fruit Descriptions\n");
            
            int choice = scanner.nextInt();

            switch (choice) {
                case 1: 
                    boolean buying = true;
                    while (buying) {
                        System.out.println("\n--- Buy Menu (Day " + days + "/10 | Current Money: $" + money + ") ---");
                        if (days >= 6) {
                            System.out.println("1. Apple | 2. Banana | 3. Cherry | 4. Pineapple | 5. Watermelon | 6. Grape | 7. Blueberry | 8. Dragon Fruit | 9. Kiwi | 10. Return | 11. Next Day");
                        } else {
                            System.out.println("1. Apple | 2. Banana | 3. Cherry | 4. Pineapple | 5. Watermelon | 6. Grape | 7. Blueberry | 8. Return | 9. Next Day");
                        }
                        int buychoice = scanner.nextInt();

                        int returnBuyChoice = (days >= 6) ? 10 : 8;
                        int nextDayBuyChoice = (days >= 6) ? 11 : 9;

                        if (buychoice == returnBuyChoice) {
                            buying = false;
                        } else if (buychoice == nextDayBuyChoice) {
                            days = advanceDay(days, pineapplesBoughtToday, pineapplesBonusNextDay, watermelonAges, blueberryAges, playerInventory);
                            pineapplesBoughtToday = 0;
                            
                            if (days <= 10) {
                                System.out.println("Onto the next day...\n");
                                recievedDailyMoney = false;
                                newDay = true;
                                buying = false;
                            } else {
                                displayGameOver(score, money, scanner);
                                return;
                            }
                        } else {
                            int index = buychoice - 1;
                            if (index >= 0 && index < displayLimit) {
                                System.out.println("How many would you like to purchase?");
                                int numberoffruit = scanner.nextInt();

                                int itemPrice = price[index];
                                boolean isSurge = isSurgeFruit(days, index, surgeDays, surgeFruits);
                                boolean isSweetDemand = (index == 2 && isCherrySweetDemandDay(days, sweetDemandDays));

                                if (isSweetDemand) {
                                    itemPrice *= 2; 
                                } else if (days == saleDay && index == saleFruitIndex) {
                                    itemPrice /= 2;
                                } else if (isSurge) {
                                    itemPrice *= 2;
                                }

                                int totalCost = itemPrice * numberoffruit;
                                int effectiveMaxStock = isSurge ? Math.max(1, maxStock[index] / 2) : maxStock[index];
                                int effectiveStock = Math.min(stock[index], effectiveMaxStock);
                                
                                if (totalCost > money) {
                                    System.out.println("You do not have enough funds for this purchase.");
                                } else if (numberoffruit > effectiveStock) {
                                    System.out.println("Not enough stock available!");
                                } else {
                                    System.out.println("Thank you for your purchase!");
                                    money -= totalCost;
                                    stock[index] -= numberoffruit;
                                    playerInventory[index] += numberoffruit;
                                    score += totalCost;
                                    
                                    if (index == 7) dragonFruitBought += numberoffruit;
                                    if (index == 3) pineapplesBoughtToday += numberoffruit;
                                    if (index == 4) watermelonAges[2] += numberoffruit; 
                                    if (index == 6) blueberryAges[0] += numberoffruit;
                                }
                            } else {
                                System.out.println("Invalid fruit choice.");
                            }
                        }
                    }
                    break;

                case 2: 
                    boolean selling = true;
                    while (selling) {
                        System.out.println("\n--- Sell Menu (Day " + days + "/10 | Current Money: $" + money + ") ---");
                        double kiwiMultiplier = 1.0 + (playerInventory[8] * 0.15);

                        for (int i = 0; i < displayLimit; i++) {
                            int currentSellPrice = sellPrice[i];
                            boolean isCherrySweetDemand = (i == 2 && isCherrySweetDemandDay(days, sweetDemandDays));
                            boolean isSurge = isSurgeFruit(days, i, surgeDays, surgeFruits);

                            if (i == 0 && days % 2 == 0) currentSellPrice *= 2;
                            else if (i == 1 && days % 2 != 0) currentSellPrice = 6;
                            else if (isCherrySweetDemand) currentSellPrice = sellPrice[2] * 2; 
                            else if (i == 4) currentSellPrice = watermelonDailySellPrice;
                            else if (i == 5) {
                                currentSellPrice = 14; // default preview ($14-$22 bulk range)
                            } else if (i == 6) currentSellPrice = 20;

                            if (isSurge) {
                                currentSellPrice *= 2;
                            }

                            if (i != 7 && i != 8) {
                                currentSellPrice = (int) Math.round(currentSellPrice * kiwiMultiplier);
                            }

                            String priceFormatted = String.format("$%3d", currentSellPrice);
                            if (i == 5) priceFormatted = "$14-$22";

                            if (isCherrySweetDemand) {
                                priceFormatted = PURPLE + priceFormatted + RESET;
                            } else if (isSurge) {
                                priceFormatted = YELLOW + priceFormatted + RESET;
                            }

                            System.out.printf("%2d. %-15s | Base Sell Price: %s | In Inventory: %2d\n", 
                                (i + 1), fruits[i], priceFormatted, playerInventory[i]);
                        }
                        
                        int returnSellChoice = (days >= 6) ? 10 : 8;
                        int nextDaySellChoice = (days >= 6) ? 11 : 9;
                        System.out.println(returnSellChoice + ". Return | " + nextDaySellChoice + ". Next Day");
                        
                        int sellChoice = scanner.nextInt();
                        if (sellChoice == returnSellChoice) {
                            selling = false;
                        } else if (sellChoice == nextDaySellChoice) {
                            days = advanceDay(days, pineapplesBoughtToday, pineapplesBonusNextDay, watermelonAges, blueberryAges, playerInventory);
                            pineapplesBoughtToday = 0;

                            if (days <= 10) {
                                System.out.println("Onto the next day...\n");
                                recievedDailyMoney = false;
                                newDay = true;
                                selling = false;
                            } else {
                                displayGameOver(score, money, scanner);
                                return;
                            }
                        } else {
                            int indexSell = sellChoice - 1;
                            if (indexSell >= 0 && indexSell < displayLimit) {
                                System.out.println("How many " + fruits[indexSell] + "(s) would you like to sell?");
                                int sellQuantity = scanner.nextInt();

                                int maxSellable = playerInventory[indexSell];
                                if (indexSell == 4) maxSellable = watermelonAges[0] + watermelonAges[1];

                                if (sellQuantity > maxSellable) {
                                    if (indexSell == 4) {
                                        System.out.println("You cannot sell watermelons on the same day you buy them! Eligible: " + maxSellable);
                                    } else {
                                        System.out.println("You can't do that!");
                                    }
                                } else if (sellQuantity <= 0) {
                                    System.out.println("Invalid quantity.");
                                } else {
                                    int earnings = 0;
                                    boolean isSurgeSell = isSurgeFruit(days, indexSell, surgeDays, surgeFruits);

                                    if (indexSell == 6) {
                                        int remainingToSell = sellQuantity;
                                        for (int k = 5; k >= 0 && remainingToSell > 0; k--) {
                                            int batchBasePrice = Math.min(50, 20 + (k * 8));
                                            if (isSurgeSell) {
                                                batchBasePrice *= 2;
                                            }
                                            int batchFinalPrice = (int) Math.round(batchBasePrice * kiwiMultiplier);
                                            int sellFromBatch = Math.min(remainingToSell, blueberryAges[k]);
                                            
                                            earnings += batchFinalPrice * sellFromBatch;
                                            blueberryAges[k] -= sellFromBatch;
                                            remainingToSell -= sellFromBatch;
                                        }
                                        playerInventory[6] = blueberryAges[0] + blueberryAges[1] + blueberryAges[2] + 
                                                             blueberryAges[3] + blueberryAges[4] + blueberryAges[5];
                                    } else {
                                        int actualSellPrice = sellPrice[indexSell];
                                        boolean isCherrySweetDemandSell = (indexSell == 2 && isCherrySweetDemandDay(days, sweetDemandDays));

                                        if (indexSell == 0 && days % 2 == 0) actualSellPrice *= 2;
                                        else if (indexSell == 1 && days % 2 != 0) actualSellPrice = 6;
                                        else if (isCherrySweetDemandSell) actualSellPrice = sellPrice[2] * 2;
                                        else if (indexSell == 4) actualSellPrice = watermelonDailySellPrice;
                                        else if (indexSell == 5) {
                                            if (sellQuantity >= 10) actualSellPrice = 22;
                                            else if (sellQuantity >= 5) actualSellPrice = 18;
                                            else actualSellPrice = 14;
                                        }

                                        if (isSurgeSell) {
                                            actualSellPrice *= 2;
                                        }

                                        if (indexSell != 7 && indexSell != 8) {
                                            actualSellPrice = (int) Math.round(actualSellPrice * kiwiMultiplier);
                                        }
                                        earnings = actualSellPrice * sellQuantity;
                                    }

                                    money += earnings;
                                    playerInventory[indexSell] -= sellQuantity;
                                    stock[indexSell] += sellQuantity;

                                    if (indexSell == 4) {
                                        int remainingToSell = sellQuantity;
                                        for (int k = 0; k < 2 && remainingToSell > 0; k++) {
                                            if (watermelonAges[k] >= remainingToSell) {
                                                watermelonAges[k] -= remainingToSell;
                                                remainingToSell = 0;
                                            } else {
                                                remainingToSell -= watermelonAges[k];
                                                watermelonAges[k] = 0;
                                            }
                                        }
                                        playerInventory[4] = watermelonAges[0] + watermelonAges[1] + watermelonAges[2];
                                    }
                                    
                                    score = Math.max(0, score - (price[indexSell] * sellQuantity));
                                    System.out.println("Successfully sold " + sellQuantity + " " + fruits[indexSell] + "(s) for $" + earnings + "!");
                                }
                            } else {
                                System.out.println("Returning you back.");
                            }
                        }
                    }
                    break;

                case 3: 
                    days = advanceDay(days, pineapplesBoughtToday, pineapplesBonusNextDay, watermelonAges, blueberryAges, playerInventory);
                    pineapplesBoughtToday = 0;

                    if (days <= 10) {
                        System.out.println();
                        System.out.println("Onto the next day...\n");
                        Thread.sleep(1000);
                        recievedDailyMoney = false;
                        newDay = true;
                    } else {
                        displayGameOver(score, money, scanner);
                        return;
                    }
                    break;

                case 4: 
                    System.out.println("Thanks for playing!");
                    Thread.sleep(1000);
                    scanner.close();
                    return;

                case 5: 
                    for (int i = 0; i < displayLimit; i++) {
                        System.out.printf("%2d. %-15s | In Inventory: %2d\n", (i + 1), fruits[i], playerInventory[i]);
                    }
                    System.out.println("\nPress 1 to return");
                    if (scanner.nextInt() == 1) break;
                    break;

                case 6: 
                    boolean viewingDescriptions = true;
                    while (viewingDescriptions) {
                        System.out.println("\n--- Fruit Descriptions ---");
                        if (days >= 6) {
                            System.out.println("1. Apple | 2. Banana | 3. Cherry | 4. Pineapple | 5. Watermelon | 6. Grape | 7. Blueberry | 8. Dragon Fruit | 9. Kiwi | 10. Return");
                        } else {
                            System.out.println("1. Apple | 2. Banana | 3. Cherry | 4. Pineapple | 5. Watermelon | 6. Grape | 7. Blueberry | 8. Return");
                        }
                        int descChoice = scanner.nextInt();
                        
                        int returnDescChoice = (days >= 6) ? 10 : 8;
                        if (descChoice == returnDescChoice) {
                            viewingDescriptions = false;
                        } else {
                            switch (descChoice) {
                                case 1: System.out.println("Apples sell for double the regular amount on even number days.\n"); break;
                                case 2: System.out.println("Bananas sell for $6 on odd number days.\n"); break;
                                case 3: System.out.println("Cherries experience Sweet Demand days costing double to buy and sell for double their regular price! Base sell price is $8.\n"); break;
                                case 4: System.out.println("Pineapple increases your starting money for the next day ($45 extra per pineapple).\n"); break;
                                case 5: System.out.println("Watermelons are high-risk investments! Cannot be sold on the day bought, and spoil after 3 days.\n"); break;
                                case 6: System.out.println("Buy in bulk! Costs $15 with high-reward bulk resale tiers ($14 / $18 / $22).\n"); break;
                                case 7: System.out.println("Compound Holding! Sell value increases by +$8 for every day held in inventory (caps at $50 after 4 days).\n"); break;
                                case 8: 
                                    if (days >= 6) System.out.println("The king of fruit. Costs $80, grants +$20/day allowance, and resells for $30.\n");
                                    else viewingDescriptions = false;
                                    break;
                                case 9: System.out.println("Market Booster! Each Kiwi in your inventory increases the resale value of ALL other fruits by +15%!\n"); break;
                                default: System.out.println("Invalid choice. Please try again.\n");
                            }
                        }
                    }
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}