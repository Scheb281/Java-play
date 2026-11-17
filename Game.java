import java.util.Random;
import java.util.Scanner;

public class Game{
    public static int clearedLocations;
    public static boolean finished;
    private static Player player;
    private static final String[] locations = {"Лес", "Пещера", "Руины"};

    private static final Scanner input = new Scanner(System.in, "UTF-8");
    private static final Random random = new Random();

    static void showMenu() {
        System.out.println("\n1. Исследовать мир");
        System.out.println("2. Характеристики");
        System.out.println("3. Инвентарь");
        System.out.println("4. Магазин");
        System.out.println("5. Отдохнуть (10 золота)");
        System.out.println("0. Выйти");
    }

    
    static void openShop() {
        int[] prices = {15, 35, 50, 50};
        while (!finished) {
            System.out.println("\nМагазин. Ваше золото: " + player.gold);
            System.out.println("1. Малое зелье (+30 HP) — " + prices[0]);
            System.out.println("2. Большое зелье (+70 HP) — " + prices[1]);
            System.out.println("3. Камень силы (+5 атаки) — " + prices[2]);
            System.out.println("4. Защитный амулет (+5 защиты) — " + prices[3]);
            System.out.println("0. Назад");

            int choice = readChoice(0, 4);
            if (choice <= 0) {
                return;
            }
            if (!player.spendGold(prices[choice - 1])) {
                continue;
            }
            switch (choice) {
                case 1-> player.smallPotions++;
                case 2 -> player.largePotions++;
                case 3 -> player.attack += 5;
                case 4 -> player.defense += 5;
            }
            System.out.println("Покупка совершена.");
        }
    }
    static void randomEvent() {
        switch (random.nextInt(6)) {
            case 0->
            {
                int foundGold = 20 + random.nextInt(21);
                player.gold += foundGold;
                System.out.println("Вы нашли сундук. Внутри " + foundGold + " золота.");
            }
            case 1-> startBattle("Разбойник", 35, 9, 20, 20);
            case 2->{
                player.smallPotions++;
                System.out.println("Вы нашли малое зелье.");
            }
            case 3 ->{
                int damage = Math.min(player.hp, 10 + random.nextInt(11));
                player.hp -= damage;
                System.out.println("Ловушка! Потеряно " + damage + " HP.");
            }
            case 4 ->{
                System.out.println("Вам встретился странствующий торговец.");
                openShop();
            }
            default -> System.out.println("Ничего не произошло.");
        }
    }
    static void rest() {
        if (player.hp == player.maxHp) {
            System.out.println("Здоровье уже полное.");
            return;
        }
        if (player.spendGold(10)) {
            player.heal(player.maxHp * 30 / 100);
        }
    }
    static boolean checkGameOver() {
        return player.hp <= 0;
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
    static void startGame(){
        System.out.println("Дорога к древнему замку");
        System.out.println("Выберите класс:");
        System.out.println("1. Чернокнижник");
        System.out.println("2. Войн");
        System.out.println("3. Вор");
        

        int classses = readChoice(1, 3);

        switch (classses) {
            case 1 -> player = new Warlock();
            case 2 -> player = new Wars();
            case 3 -> player = new Thief();
            default ->  player = new Wars();
        }

        while (!finished && !checkGameOver()) {
            showMenu();
            switch (readChoice(0, 5)) {
                case 1-> startAdventure();
                case 2 -> player.showStats();
                case 3 ->{
                    player.showInventory();
                    player.usePotion();
                }
                case 4 -> openShop();
                case 5 -> rest();
                default -> finished = true;
            }
        }

        if (checkGameOver()) {
            System.out.println("\nПоражение. " + player.name + " пал в бою или не пережил приключение.");
        }
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
            case 0 -> startBattle("Лесной волк", 45, 10, 25, 40);
            case 1 -> startBattle("Пещерный тролль", 80, 15, 40, 60);
            case 2 -> startBattle("Страж руин", 120, 19, 65, 90);
            default ->{
                return;
            }
        }
        if (!finished && !checkGameOver()) {
            clearedLocations++;
            System.out.println("Локация пройдена.");
            if (clearedLocations == locations.length) {
                System.out.println("Открылся путь в древний замок.");
            }
        }
    }

    static void startBattle(String enemyName, int enemyHp, int enemyAttack, int enemyRewardGold, int enemyRewardExp) {
        if (fight(enemyName, enemyHp, enemyAttack, 0, false)) {
            player.gold += enemyRewardGold;
            player.exp += enemyRewardExp;
            System.out.printf("Победа! Получено %d золота и %d опыта.%n",
                    enemyRewardGold, enemyRewardExp);
            player.levelUp();
        }
    }

    private static boolean fight(String enemyName, int enemyHp, int enemyAttack, int enemyDefense, boolean boss) {
        System.out.println("\nПротивник: " + enemyName);
        int maxEnemyHp = enemyHp;
        int enemyTurn = 0;
        boolean strongAvailable = true;
        boolean enraged = false;

        while (enemyHp > 0 && !checkGameOver()) {
            System.out.printf("%s: %d HP | %s: %d/%d HP%n", enemyName, enemyHp, player.name, player.hp, player.maxHp);
            System.out.println("1. Атака");
            System.out.println("2. Сильный удар" + (strongAvailable ? "" : " (восстанавливается)"));
            System.out.println("3. Выпить зелье");
            int choice = readChoice(1, 3);
            if (choice < 0) {
                return false;
            }

            if (choice == 3) {
                if (!player.drinkPotion()) {
                    continue;
                }
                strongAvailable = true;
            } else {
                if (choice == 2 && !strongAvailable) {
                    System.out.println("Сильный удар пока недоступен.");
                    continue;
                }
                int hitAttack = choice == 2 ? player.attack * 3 / 2 : player.attack;
                int damage = player.calculateDamage(hitAttack, enemyDefense);
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
            int damage = player.calculateDamage(hitAttack, player.defense);
            player.hp = Math.max(0, player.hp - damage);
            System.out.println(enemyName + " — " + move + ": " + damage + " урона.");
        }
        return false;
    }
    public static int readChoice(int min, int max) {
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