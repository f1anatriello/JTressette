/*
 * Asset manager vecchio, la tengo giusto se dovesse servire un rollback
 * DA RICREARE UNA CLASSE PER IL RENDERING DELLE CARTE
 */



//package ui;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.InputStream;
//import java.util.HashMap;
//import java.util.Map;
//
//
//public final class Assets {
//
// private static final Assets I = new Assets();
// public static Assets get() { return I; }
//
// private final Map<String, Image> cache = new HashMap<>();
// private float scale = 1.0f;
//
// // Base path nel classpath (src/main/resources o resources/)
// private String basePath = "/images/"; // es: /assets/cards/denari_A.png
//
// private Assets() {
//	 caricaImmaginiCarta(basePath);
//	 
// }
//
// public void setBasePath(String basePathClasspath) {
//     if (!basePathClasspath.endsWith("/")) basePathClasspath += "/";
//     this.basePath = basePathClasspath;
// }
//
// public void setScale(float scale) { this.scale = Math.max(0.5f, Math.min(scale, 3f)); }
//
// public Image get(String name) {
//     return cache.computeIfAbsent(name, this::caricaImmaginiCarta);
// }
//
// public Image card(String seme, String valore) {
//     // convenzione nomi: denari_A.png  coppe_3.png  bastoni_F.png  spade_R.png
//     return get("cards/" + valore.toLowerCase() + "_" + seme.toUpperCase() + ".jpg");
// }
//
// public Image cardBack() { return get("background/Napoletane_retro.jpg"); }
//
// public Image table() { return get("table/tavolo.jpg"); } 
//
// public Image icon(String name) { return get("icons/" + name + ".png"); }
//
// private Image caricaImmaginiCarta(String rel) {
//     String full = basePath + rel;
//     try (InputStream is = Assets.class.getResourceAsStream(full)) {
//         if (is == null) throw new IllegalStateException("Asset non trovato: " + full);
//         BufferedImage raw = ImageIO.read(is);
//         if (scale != 1f) {
//             int w = Math.max(1, Math.round(raw.getWidth() * scale));
//             int h = Math.max(1, Math.round(raw.getHeight() * scale));
//             BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//             Graphics2D g = scaled.createGraphics();
//             g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//             g.drawImage(raw, 0, 0, w, h, null);
//             g.dispose();
//             return scaled;
//         }
//         return raw;
//     } catch (Exception ex) {
//         System.err.println("Errore caricando " + full + ": " + ex.getMessage());
//         // fallback 1x1 trasparente
//         return new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
//     }
//   }
// 
//
// 
// 
//}
//
package rollback;


