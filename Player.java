import java.util.Scanner;

public class Player{

    public String name;
    public int maxHp = 100;
    public int hp = this.maxHp;
    public int attack = 18;
    public int defense = 4;
    public int gold = 30;
    public int level = 1;
    public int exp;
    public int smallPotions = 1;
    public int largePotions;
    public String className;

    private static final Scanner input = new Scanner(System.in, "UTF-8");
    
    public Player(){
        System.out.print("Имя героя: ");
        if (!input.hasNextLine()) {
            return;
        }
        this.name = input.nextLine().trim();
        if (this.name.isEmpty()) {
            this.name = "Путник";
        }
    }

    public int calculateDamage(int attack, int defense) {
        return Math.max(1, attack - defense);
    }

    public void usePotion() {
        this.drinkPotion();
    }

    public boolean drinkPotion() {
        if (this.hp == this.maxHp) {
            System.out.println("Здоровье уже полное.");
            return false;
        }
        System.out.printf("1. Малое зелье (%d)%n2. Большое зелье (%d)%n0. Назад%n", this.smallPotions, this.largePotions);
        int choice = Game.readChoice(0, 2);
        if (choice <= 0) {
            return false;
        }
        if (choice == 1 && this.smallPotions > 0) {
            this.smallPotions--;
            this.heal(30);
        } else if (choice == 2 && this.largePotions > 0) {
            this.largePotions--;
            this.heal(70);
        } else {
            System.out.println("Такого зелья нет.");
            return false;
        }
        return true;
    }

    public void showInventory() {
        System.out.printf("Малые зелья: %d | Большие зелья: %d%n", this.smallPotions, this.largePotions);
    }

    public void heal(int amount) {
        int restored = Math.min(amount, this.maxHp - this.hp);
        this.hp += restored;
        System.out.printf("Восстановлено %d HP. Здоровье: %d/%d.%n", restored, this.hp, this.maxHp);
    }

    public void showStats() {
        System.out.printf("%s | HP %d/%d | Атака %d | Защита %d | Золото %d%n", this.name, this.hp, this.maxHp, this.attack, this.defense, this.gold);
        System.out.printf("Уровень %d | Опыт %d/%d%n", this.level, this.exp, this.level * 50);
    }

    public boolean spendGold(int amount) {
        if (this.gold < amount) {
            System.out.println("Недостаточно золота.");
            return false;
        }
        this.gold -= amount;
        return true;
    }

    public void levelUp() {
        while (this.exp >= this.level * 50) {
            this.exp -= this.level * 50;
            this.level++;
            this.maxHp += 20;
            this.attack += 4;
            this.defense += 2;
            this.hp = this.maxHp;
            System.out.println("Новый уровень: " + this.level + ". Здоровье полностью восстановлено.");
        }
    }
}