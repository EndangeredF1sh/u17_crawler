import pers.EndangeredFish.u17_crawler.imageUrlParser.*;
import pers.EndangeredFish.u17_crawler.introUrlParser.*;

/**
 * 主类，入口函数
 * @author EndangeredFish
 * @version 1.0
 */
public class Main{
    public static void main(String[] args){
        IntroMainClass i = new IntroMainClass();
        i.run();
        ImageMainClass imc = new ImageMainClass();
        imc.run();
    }
}

