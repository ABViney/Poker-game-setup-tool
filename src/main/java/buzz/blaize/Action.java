package buzz.blaize;

import jdk.jfr.Label;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public enum Action {
    @Label("Create new game")
    CreateGame,

    @Label("Add bots")
    AddBot,
    @Label("Remove bots")
    RemoveBot,

    @Label("Start game")
    StartGame,

    @Label("Stop game")
    StopGame,

    @Label("Quit")
    Quit;

    public String getLabel() {
        try {
            Field field = this.getClass().getField(this.name());
            Label label = field.getAnnotation(Label.class);
            return label.value();
        } catch (NoSuchFieldException e) {
            // This does not happen
            return "This is impossible.";
        }
    }
}
