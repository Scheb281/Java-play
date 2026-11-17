public class Warlock extends Player{
    public Warlock(){
        super();
        this.maxHp = 70;
        this.attack = 24;
        this.defense = 2;
        this.gold = 40;
        this.smallPotions = 2;
        this.largePotions = 1;
        this.className = "Чернокнижник";
    }

    @Override
    public void showStats(){
        System.out.printf("%s | HP %d/%d | Атака %d | Защита %d | Золото %d%n | Класс %s | ", this.name, this.hp, this.maxHp, this.attack, this.defense, this.gold, this.className);
        System.out.printf("Уровень %d | Опыт %d/%d%n", this.level, this.exp, this.level * 50);
    }
}