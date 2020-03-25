import com.sun.xml.internal.ws.commons.xmlutil.Converter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.*;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Distance;
import org.rspeer.runetek.api.commons.math.DistanceEvaluator;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.menu.tree.NpcAction;
import org.rspeer.runetek.api.input.menu.tree.PlayerAction;
import org.rspeer.runetek.api.input.menu.tree.WalkAction;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.House;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.runetek.api.scene.Players;

import org.rspeer.script.Script;
import org.rspeer.script.ScriptCategory;
import org.rspeer.script.ScriptMeta;
import org.rspeer.ui.Log;



@ScriptMeta(name = "one", desc = "Kills goblins in Lumbridge", developer = "me", version = 1.1, category = ScriptCategory.COMBAT)
public class Main extends Script {
    public String myTask = "attack";

    @Override
    public void onStart() {
        Log.fine("This will be executed once on startup.");
        super.onStart();
    }

    @Override
    public int loop() {
        Player me = Players.getLocal();
        String NPC_NAME = "Goblin";
        Npc goblinTarget = Npcs.getNearest("Goblin");

        switch (myTask) {
            case "attack":
                if (!Players.getLocal().isHealthBarVisible() && !Players.getLocal().isMoving() && !Players.getLocal().isAnimating() && !goblinTarget.isHealthBarVisible()) {
                    Log.fine("Attacking goblin");
                    goblinTarget.click();
                    myTask = "bones";
                    Time.sleepUntil(() -> !Players.getLocal().isHealthBarVisible(), 500, 10000);
                    break;
                }
                else if (goblinTarget.isHealthBarVisible() && !Players.getLocal().isMoving() && !Players.getLocal().isAnimating()) {
                    Log.fine("Closest goblin under attack, waiting");
                    myTask = "attack";
                    break;
                }

            case "bones":
                Time.sleepUntil(() -> !Players.getLocal().isHealthBarVisible(), 500, 1000000);
                Pickable nearBone = Pickables.getNearest("Bones");
                // Pick up the nearest bone if it is within 2 spaces
                if (nearBone.getPosition().distance(Players.getLocal().getPosition()) <= 2 && !Inventory.isFull() && !Players.getLocal().isHealthBarVisible() && !Players.getLocal().isMoving() && !Players.getLocal().isAnimating()) {
                    Log.fine("Walking to bones and picking up");
                    nearBone.click();
                    Time.sleepUntil(() -> !Players.getLocal().isMoving(), 500, 10000);
                    Time.sleep(200-300);
                    myTask = "attack";
                    break;
                }
                else if (Inventory.isFull()) {
                    Log.fine("Inventory full now burying");
                    for (Item i : Inventory.getItems()) {
                        i.interact("bury");
                        Time.sleep(1500-1800);
                    }
                    myTask = "bone";
                    break;
                }
                else {
                    Log.fine("Could not find any bones to bury");
                    myTask = "attack";
                    break;
                }
        }
    return 200;
    }
}
