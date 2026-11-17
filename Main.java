import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final Scanner input = new Scanner(System.in, "UTF-8");
    private static final Random random = new Random();
    private static final String[] locations = {"Лес", "Пещера", "Руины"};

    private static String name;
    private static int maxHp = 100;
    private static int hp = maxHp;
    private static int attack = 18;
    private static int defense = 4;
    private static int gold = 30;
    private static int level = 1;
    private static int exp;
    private static int smallPotions = 1;
    private static int largePotions;
    private static int clearedLocations;
    private static boolean finished;

    public static void main(String[] args) {
        System.out.println("Дорога к древнему замку");
        System.out.print("Имя героя: ");
        if (!input.hasNextLine()) {
            return;
        }
        name = input.nextLine().trim();
        if (name.isEmpty()) {
            name = "Путник";
        }

        while (!finished && !checkGameOver()) {
            showMenu();
            switch (readChoice(0, 5)) {
                case 1:
                    startAdventure();
                    break;
                case 2:
                    showStats(name, hp, maxHp, attack, defense, gold, level, exp);
                    break;
                case 3:
                    showInventory();
                    usePotion();
                    break;
                case 4:
                    openShop();
                    break;
                case 5:
                    rest();
                    break;
                default:
                    finished = true;
                    break;
            }
        }

        if (checkGameOver()) {
            System.out.println("\nПоражение. " + name + " пал в бою или не пережил приключение.");
        }
    }

    static void showMenu() {
        System.out.println("\n1. Исследовать мир");
        System.out.println("2. Характеристики");
        System.out.println("3. Инвентарь");
        System.out.println("4. Магазин");
        System.out.println("5. Отдохнуть (10 золота)");
        System.out.println("0. Выйти");
    }

    static void showStats(String name, int hp, int maxHp, int attack, int defense,
                          int gold, int level, int exp) {
        System.out.printf("%s | HP %d/%d | Атака %d | Защита %d | Золото %d%n",
                name, hp, maxHp, attack, defense, gold);
        System.out.printf("Уровень %d | Опыт %d/%d%n", level, exp, level * 50);
    }

    static void startAdventure() {
        for (int i = 0; i < locations.length; i++) {
            String status = i > clearedLocations ? " (закрыто)" : "";
            System.out.println((i + 1) + ". " + locations[i] + status);
        }
        boolean castleOpen = clearedLocations == locations.length;
        System.out.println("4. Древний замок" + (castleOpen ? "" : " (закрыто)"));
        System.out.println("0. Назад");

        int choice = readChoice(0, 4);
        if (choice <= 0) {
            return;
        }
        if (choice > clearedLocations + 1) {
            System.out.println("Сначала пройдите предыдущую локацию.");
            return;
        }
        if (choice == 4) {
            finalBoss();
            return;
        }

        int location = choice - 1;
        System.out.println("\nВы исследуете локацию «" + locations[location] + "».");
        randomEvent();
        if (finished || checkGameOver() || location < clearedLocations) {
            return;
        }

        switch (location) {
            case 0:
                startBattle("Лесной волк", 45, 10, 25, 40);
                break;
            case 1:
                startBattle("Пещерный тролль", 80, 15, 40, 60);
                break;
            case 2:
                startBattle("Страж руин", 120, 19, 65, 90);
                break;
            default:
                return;
        }
        if (!finished && !checkGameOver()) {
            clearedLocations++;
            System.out.println("Локация пройдена.");
            if (clearedLocations == locations.length) {
                System.out.println("Открылся путь в древний замок.");
            }
        }
    }

    static void startBattle(String enemyName, int enemyHp, int enemyAttack,
                            int enemyRewardGold, int enemyRewardExp) {
        if (fight(enemyName, enemyHp, enemyAttack, 0, false)) {
            gold += enemyRewardGold;
            exp += enemyRewardExp;
            System.out.printf("Победа! Получено %d золота и %d опыта.%n",
                    enemyRewardGold, enemyRewardExp);
            levelUp();
        }
    }

    private static boolean fight(String enemyName, int enemyHp, int enemyAttack,
                                 int enemyDefense, boolean boss) {
        System.out.println("\nПротивник: " + enemyName);
        int maxEnemyHp = enemyHp;
        int enemyTurn = 0;
        boolean strongAvailable = true;
        boolean enraged = false;

        while (enemyHp > 0 && !checkGameOver()) {
            System.out.printf("%s: %d HP | %s: %d/%d HP%n", enemyName, enemyHp, name, hp, maxHp);
            System.out.println("1. Атака");
            System.out.println("2. Сильный удар" + (strongAvailable ? "" : " (восстанавливается)"));
            System.out.println("3. Выпить зелье");
            int choice = readChoice(1, 3);
            if (choice < 0) {
                return false;
            }

            if (choice == 3) {
                if (!drinkPotion()) {
                    continue;
                }
                strongAvailable = true;
            } else {
                if (choice == 2 && !strongAvailable) {
                    System.out.println("Сильный удар пока недоступен.");
                    continue;
                }
                int hitAttack = choice == 2 ? attack * 3 / 2 : attack;
                int damage = calculateDamage(hitAttack, enemyDefense);
                enemyHp = Math.max(0, enemyHp - damage);
                strongAvailable = choice == 1;
                System.out.println("Вы наносите " + damage + " урона.");
                if (enemyHp == 0) {
                    return true;
                }
            }

            if (boss && !enraged && enemyHp * 2 < maxEnemyHp) {
                enraged = true;
                System.out.println("Повелитель Тьмы приходит в ярость!");
            }
            enemyTurn++;
            int hitAttack = enemyAttack + (enraged ? 5 : 0);
            String move = "Обычная атака";
            if (boss && enemyTurn % (enraged ? 3 : 4) == 0) {
                move = "Волна тьмы";
                hitAttack += 12;
            } else if (boss && enemyTurn % 2 == 0) {
                move = "Сильный удар";
                hitAttack += 6;
            }
            int damage = calculateDamage(hitAttack, defense);
            hp = Math.max(0, hp - damage);
            System.out.println(enemyName + " — " + move + ": " + damage + " урона.");
        }
        return false;
    }

    static int calculateDamage(int attack, int defense) {
        return Math.max(1, attack - defense);
    }

    static void usePotion() {
        drinkPotion();
    }

    private static boolean drinkPotion() {
        if (hp == maxHp) {
            System.out.println("Здоровье уже полное.");
            return false;
        }
        System.out.printf("1. Малое зелье (%d)%n2. Большое зелье (%d)%n0. Назад%n",
                smallPotions, largePotions);
        int choice = readChoice(0, 2);
        if (choice <= 0) {
            return false;
        }
        if (choice == 1 && smallPotions > 0) {
            smallPotions--;
            heal(30);
        } else if (choice == 2 && largePotions > 0) {
            largePotions--;
            heal(70);
        } else {
            System.out.println("Такого зелья нет.");
            return false;
        }
        return true;
    }

    private static void heal(int amount) {
        int restored = Math.min(amount, maxHp - hp);
        hp += restored;
        System.out.printf("Восстановлено %d HP. Здоровье: %d/%d.%n", restored, hp, maxHp);
    }

    static void showInventory() {
        System.out.printf("Малые зелья: %d | Большие зелья: %d%n", smallPotions, largePotions);
    }

    static void openShop() {
        int[] prices = {15, 35, 50, 50};
        while (!finished) {
            System.out.println("\nМагазин. Ваше золото: " + gold);
            System.out.println("1. Малое зелье (+30 HP) — " + prices[0]);
            System.out.println("2. Большое зелье (+70 HP) — " + prices[1]);
            System.out.println("3. Камень силы (+5 атаки) — " + prices[2]);
            System.out.println("4. Защитный амулет (+5 защиты) — " + prices[3]);
            System.out.println("0. Назад");

            int choice = readChoice(0, 4);
            if (choice <= 0) {
                return;
            }
            if (!spendGold(prices[choice - 1])) {
                continue;
            }
            switch (choice) {
                case 1:
                    smallPotions++;
                    break;
                case 2:
                    largePotions++;
                    break;
                case 3:
                    attack += 5;
                    break;
                case 4:
                    defense += 5;
                    break;
                default:
                    break;
            }
            System.out.println("Покупка совершена.");
        }
    }

    private static boolean spendGold(int amount) {
        if (gold < amount) {
            System.out.println("Недостаточно золота.");
            return false;
        }
        gold -= amount;
        return true;
    }

    static void levelUp() {
        while (exp >= level * 50) {
            exp -= level * 50;
            level++;
            maxHp += 20;
            attack += 4;
            defense += 2;
            hp = maxHp;
            System.out.println("Новый уровень: " + level + ". Здоровье полностью восстановлено.");
        }
    }

    static void randomEvent() {
        switch (random.nextInt(6)) {
            case 0:
                int foundGold = 20 + random.nextInt(21);
                gold += foundGold;
                System.out.println("Вы нашли сундук. Внутри " + foundGold + " золота.");
                break;
            case 1:
                startBattle("Разбойник", 35, 9, 20, 20);
                break;
            case 2:
                smallPotions++;
                System.out.println("Вы нашли малое зелье.");
                break;
            case 3:
                int damage = Math.min(hp, 10 + random.nextInt(11));
                hp -= damage;
                System.out.println("Ловушка! Потеряно " + damage + " HP.");
                break;
            case 4:
                System.out.println("Вам встретился странствующий торговец.");
                openShop();
                break;
            default:
                System.out.println("Ничего не произошло.");
                break;
        }
    }

    static void rest() {
        if (hp == maxHp) {
            System.out.println("Здоровье уже полное.");
            return;
        }
        if (spendGold(10)) {
            heal(maxHp * 30 / 100);
        }
    }

    static boolean checkGameOver() {
        return hp <= 0;
    }

    static void finalBoss() {
        if (clearedLocations != locations.length) {
            System.out.println("Древний замок ещё закрыт.");
            return;
        }
        if (fight("Повелитель Тьмы", 300, 25, 5, true)) {
            finished = true;
            System.out.println("\nПоздравляем! Повелитель Тьмы повержен. Игра завершена.");
        }
    }

    private static int readChoice(int min, int max) {
        while (true) {
            System.out.print("> ");
            if (!input.hasNextLine()) {
                finished = true;
                return -1;
            }
            try {
                int choice = Integer.parseInt(input.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.printf("Введите число от %d до %d.%n", min, max);
        }
    }
}
