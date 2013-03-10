package sin.hud;

import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.player.Character;
import sin.hud.BarManager.BH;
import sin.tools.T;
import sin.weapons.RecoilManager;
import sin.world.CG;

/**
 * HUD (Heads Up Display) - Controls GUI elements while in-game.
 * @author SinisteRing
 */
public class HUD {
    private static GameClient app;
    
    // Constant Variables:
    private static final float CROSSHAIR_LENGTH = 16;
    private static final float CROSSHAIR_OFFSET = 5;
    private static final float CROSSHAIR_WIDTH = 3.2f;

    // Instance Variables:
    private static Node node = new Node("GUI");      // Node used to attach/detach GUI and HUD elements.
    private static float cx, cy;
    private static BitmapText ping;
    private static BitmapText loc;
    private static BitmapText fps;
    private static Geometry[] crosshair = new Geometry[4];

    public static Node getGUI(){
        return node;
    }
    public static BitmapText getPing(){
        return ping;
    }
    
    private static void createCrosshairs(float length, float offset, float width){
        crosshair[0] = CG.createLine(node, "", width, T.v3f(cx-(length+offset), cy), T.v3f(cx-offset, cy), ColorRGBA.Red);
        crosshair[1] = CG.createLine(node, "", width, T.v3f(cx, cy-(length+offset)), T.v3f(cx, cy-offset), ColorRGBA.Red);
        crosshair[2] = CG.createLine(node, "", width, T.v3f(cx+(length+offset), cy), T.v3f(cx+offset, cy), ColorRGBA.Red);
        crosshair[3] = CG.createLine(node, "", width, T.v3f(cx, cy+(length+offset)), T.v3f(cx, cy+offset), ColorRGBA.Red);
    }
    public static void updateCrosshairs(){
        float mod = RecoilManager.getSpreadMod();
        crosshair[0].setLocalTranslation(T.v3f(-mod, 0));
        crosshair[1].setLocalTranslation(T.v3f(0, -mod));
        crosshair[2].setLocalTranslation(T.v3f(mod, 0));
        crosshair[3].setLocalTranslation(T.v3f(0, mod));
    }

    public static void setBarMax(BH handle, int value){
        BarManager.setBarMax(handle, value);
    }
    public static void updateBar(BH handle, int value){
        BarManager.updateBar(handle, value);
    }
    
    public static void showCrosshairs(boolean show){
        int i = 0;
        while(i < crosshair.length){
            if(show){
                node.attachChild(crosshair[i]);
            }else{
                node.detachChild(crosshair[i]);
            }
            i++;
        }
    }

    public static void update(float tpf){
        FloatingTextManager.update(tpf);
        Vector3f ploc = Character.getLocation();
        Vector3f pdir = app.getCamera().getDirection();
        String compass;
        if(FastMath.abs(pdir.getX()) > FastMath.abs(pdir.getZ())){
            if(pdir.getX() > 0){
                compass = "North";
            }else{
                compass = "South";
            }
        }else{
            if(pdir.getZ() > 0){
                compass = "East";
            }else{
                compass = "West";
            }
        }
        loc.setText("X: "+String.format("%5.0f", ploc.getX())+"\nY: "+String.format("%5.0f", ploc.getY())+"\nZ: "+String.format("%5.0f", ploc.getZ())+"\nFacing: "+compass);
        fps.setText("FPS: " + String.format("%5.0f", 1/tpf));
        RecoilManager.updateCrosshairs();
    }
    public static void clear(){
        BarManager.clear();
        node.detachAllChildren();
    }
    
    public static void initialize(GameClient app, Node root){
        HUD.app = app;
        root.attachChild(node);
        
        // Get the center coordinates for the screen:
        cx = app.getSettings().getWidth()/2;
        cy = app.getSettings().getHeight()/2;

        // Create crosshairs:
        createCrosshairs(CROSSHAIR_LENGTH, CROSSHAIR_OFFSET, CROSSHAIR_WIDTH);

        // Create dynamic bar UI elements:
        BarManager.add(node, BH.HEALTH, 0, T.v2f(cx, 30), 200, 40, 25, ColorRGBA.Red, 100, true);
        BarManager.add(node, BH.SHIELDS, 0, T.v2f(cx, 90), 200, 40, 25, ColorRGBA.Blue, 100, true);
        BarManager.add(node, BH.AMMO_LEFT, 1, T.v2f(cx-130, 60), 30, 100, 30, ColorRGBA.Orange, 30, false);
        BarManager.add(node, BH.AMMO_RIGHT, 1, T.v2f(cx+130, 60), 30, 100, 30, ColorRGBA.Orange, 30, false);

        // Initialize ping display:
        ping = new BitmapText(T.getFont("Tele-Marines"));
        ping.setColor(ColorRGBA.Green);
        ping.setSize(16);
        ping.setLocalTranslation(T.v3f(20, cy*2-20));
        ping.setText("Not Connected");
        node.attachChild(ping);
        
        // Initialize location display:
        loc = new BitmapText(T.getFont("Batman26"));
        loc.setColor(ColorRGBA.Cyan);
        loc.setSize(14);
        loc.setLocalTranslation(T.v3f(20, cy*2-40));
        loc.setText("");
        node.attachChild(loc);
        
        fps = new BitmapText(T.getFont("Batman26"));
        fps.setColor(ColorRGBA.Red);
        fps.setSize(16);
        fps.setLocalTranslation(T.v3f(20, cy*2-100));
        fps.setText("");
        node.attachChild(fps);
    }
}
