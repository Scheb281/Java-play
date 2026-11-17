public class Wars extends Player {
    public Wars(){
        super();
        this.maxHp = 120;
        this.attack = 21;
        this.defense = 6;
        this.gold = 10;
        this.smallPotions = 1;
        this.className = "Войн";
    }
    @Override
    public void showStats(){
        System.out.printf("%s | HP %d/%d | Атака %d | Защита %d | Золото %d%n | Класс %s | ", this.name, this.hp, this.maxHp, this.attack, this.defense, this.gold, this.className);
        System.out.printf("Уровень %d | Опыт %d/%d%n", this.level, this.exp, this.level * 50);
    }
}
