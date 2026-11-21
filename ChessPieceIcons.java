import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

public class ChessPieceIcons {

    private static final String[] PREFERRED_FONTS = {
        "Segoe UI Symbol",
        "DejaVu Sans",
        "Noto Sans Symbols 2",
        "Apple Symbols",
        "Arial Unicode MS",
        Font.SERIF
    };

    private static final Map<Character, ImageIcon> CACHE = new HashMap<>();
    private static int cachedSize = -1;

    public static ImageIcon getIcon(char piece, int size) {
        if (piece == ' ') {
            return null;
        }
        if (cachedSize != size) {
            CACHE.clear();
            cachedSize = size;
        }
        return CACHE.computeIfAbsent(piece, p -> renderPieceIcon(p, size));
    }

    private static String getSolidGlyph(char piece) {
        switch (Character.toLowerCase(piece)) {
            case 'k':
                return "\u265A"; // ♚
            case 'q':
                return "\u265B"; // ♛
            case 'r':
                return "\u265C"; // ♜
            case 'b':
                return "\u265D"; // ♝
            case 'n':
                return "\u265E"; // ♞
            case 'p':
                return "\u265F"; // ♟
            default:
                return String.valueOf(piece);
        }
    }

    private static Font findChessFont(int fontSize) {
        for (String fontName : PREFERRED_FONTS) {
            Font font = new Font(fontName, Font.PLAIN, fontSize);
            if (font.canDisplay('\u265A') && font.canDisplay('\u265F')) {
                return font;
            }
        }
        return new Font(Font.SERIF, Font.PLAIN, fontSize);
    }

    private static ImageIcon renderPieceIcon(char piece, int size) {
        BufferedImage image = new BufferedImage(
            size,
            size,
            BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY
        );
        g2.setRenderingHint(
            RenderingHints.KEY_STROKE_CONTROL,
            RenderingHints.VALUE_STROKE_PURE
        );

        boolean isWhite = Character.isUpperCase(piece);
        String glyph = getSolidGlyph(piece);
        Font font = findChessFont((int) (size * 0.78));

        FontRenderContext frc = g2.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, glyph);
        Shape outline = gv.getOutline();
        Rectangle2D bounds = outline.getBounds2D();

        double tx = (size - bounds.getWidth()) / 2.0 - bounds.getX();
        double ty = (size - bounds.getHeight()) / 2.0 - bounds.getY();
        Shape centered = AffineTransform.getTranslateInstance(tx, ty)
            .createTransformedShape(outline);

        // Subtle drop shadow
        Shape shadow = AffineTransform.getTranslateInstance(1.5, 2.0)
            .createTransformedShape(centered);
        g2.setColor(new Color(0, 0, 0, 65));
        g2.fill(shadow);

        if (isWhite) {
            // White piece: crisp ivory fill with dark outline
            g2.setColor(new Color(252, 250, 245));
            g2.fill(centered);
            g2.setColor(new Color(30, 30, 30));
            g2.setStroke(
                new BasicStroke(
                    2.2f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
                )
            );
            g2.draw(centered);
        } else {
            // Black piece: subtle light rim so it stands out on dark squares, then rich dark fill
            g2.setColor(new Color(240, 240, 240, 140));
            g2.setStroke(
                new BasicStroke(
                    2.0f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
                )
            );
            g2.draw(centered);
            g2.setColor(new Color(28, 28, 32));
            g2.fill(centered);
        }

        g2.dispose();
        return new ImageIcon(image);
    }
}
