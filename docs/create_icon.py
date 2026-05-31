from PIL import Image, ImageDraw, ImageFont
import os

def create_icon():
    # Size and colors
    size = (256, 256)
    bg_color = (0, 102, 204)
    fg_color = (255, 255, 255)

    # Create base image
    image = Image.new("RGBA", size, bg_color)
    draw = ImageDraw.Draw(image)

    # Draw rounded rectangle in the center
    # Bounding box for rounded rectangle
    margin = 30
    rect_coords = [margin, margin, size[0] - margin, size[1] - margin]
    draw.rounded_rectangle(rect_coords, radius=30, outline=fg_color, width=8)

    # Try to load a font, otherwise use default
    try:
        font_large = ImageFont.truetype("arialbd.ttf", 100)
        font_small = ImageFont.truetype("arial.ttf", 40)
    except IOError:
        font_large = ImageFont.load_default()
        font_small = ImageFont.load_default()

    # Draw "S"
    s_text = "S"
    s_bbox = draw.textbbox((0, 0), s_text, font=font_large)
    s_width = s_bbox[2] - s_bbox[0]
    s_height = s_bbox[3] - s_bbox[1]
    s_x = (size[0] - s_width) / 2
    s_y = (size[1] - s_height) / 2 - 30 # shift up slightly
    draw.text((s_x, s_y), s_text, fill=fg_color, font=font_large)

    # Draw "mart"
    m_text = "mart"
    m_bbox = draw.textbbox((0, 0), m_text, font=font_small)
    m_width = m_bbox[2] - m_bbox[0]
    m_x = (size[0] - m_width) / 2
    m_y = s_y + s_height + 10
    draw.text((m_x, m_y), m_text, fill=fg_color, font=font_small)

    # Save as PNG
    png_path = os.path.join("docs", "smartmart.png")
    image.save(png_path, format="PNG")
    print(f"Saved PNG to {png_path}")

    # Save as ICO with multiple sizes
    ico_path = os.path.join("docs", "smartmart.ico")
    icon_sizes = [(16, 16), (32, 32), (48, 48), (64, 64), (128, 128), (256, 256)]
    image.save(ico_path, format="ICO", sizes=icon_sizes)
    print(f"Saved ICO to {ico_path}")

if __name__ == "__main__":
    create_icon()
