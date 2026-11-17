public class Thief extends Player {
    public Thief(){
        super();
        this.maxHp = 80;
        this.attack = 19;
        this.defense = 1;
        this.gold = 70;
        this.smallPotions = 1;
        this.className = "Вор";
    }
    @Override
    public void showStats(){
        System.out.printf("%s | HP %d/%d | Атака %d | Защита %d | Золото %d%n | Класс %s | ", this.name, this.hp, this.maxHp, this.attack, this.defense, this.gold, this.className);
        System.out.printf("Уровень %d | Опыт %d/%d%n", this.level, this.exp, this.level * 50);
    }
}
