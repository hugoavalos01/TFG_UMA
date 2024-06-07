export class NumberToClassUtil {
    static numberToClassMap = {
        0: "Vaca",
        1: "Cabra doméstica",
        2: "Ciervo",
        3: "Caballo",
        4: "Cabra montés",
        5: "Gamo",
        6: "Muflón",
        7: "Corzo",
        8: "Jabalí",
        9: "Oveja",
        10: "Lince Ibérico",
        11: "Gato montés",
        12: "Gato doméstico",
        13: "Perro",
        14: "Zorro",
        15: "Liebre",
        16: "Conejo",
        17: "Ratón",
        18: "Rata negra",
        19: "Gineta",
        20: "Meloncillo",
        21: "Garduña",
        22: "Tejón",
        23: "Nutria"
    };

    static transformNumberToWord(number) {
        return this.numberToClassMap[number] || "Desconocido";
    }
}