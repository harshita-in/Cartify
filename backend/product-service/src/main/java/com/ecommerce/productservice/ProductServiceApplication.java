package com.ecommerce.productservice;

import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.math.RoundingMode;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    private void addProduct(ProductRepository repo, String name, String desc, double price, int stock, String cat, String img) {
        repo.save(new Product(name, desc, BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP), stock, cat, img));
    }

    @Bean
    public CommandLineRunner seedProducts(ProductRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                // CATEGORY: Electronics (20 items)
                addProduct(repo, "Cyberpunk Mechanical Keyboard", "Wireless mechanical keyboard with custom hot-swappable switches and RGB.", 8999.00, 15, "Electronics", "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Noise Cancelling Headphones", "Over-ear studio headphones featuring active noise cancellation.", 12499.00, 20, "Electronics", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Smart Fitness Watch", "AMOLED health tracker monitoring heart rate, sleep quality, and active GPS.", 6999.00, 25, "Electronics", "https://images.unsplash.com/photo-1579586337278-3befd40fd17a?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Ultra-Wide Curved Monitor", "34-inch ultra-wide curved gaming monitor with 144Hz refresh rate.", 27999.00, 10, "Electronics", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Professional Condenser Mic", "USB condenser microphone cardoid pattern for streaming and podcasts.", 5499.00, 14, "Electronics", "https://images.unsplash.com/photo-1590602847861-f357a9332bbc?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Full HD Streaming Webcam", "1080p 60fps webcam with auto-focus and dual stereo noise-reducing mics.", 4499.00, 18, "Electronics", "https://images.unsplash.com/photo-1600541519401-4471f5587ba5?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Ergonomic Wireless Mouse", "High precision 16000 DPI wireless mouse with programmable side keys.", 3999.00, 22, "Electronics", "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Dual Device Charging Pad", "15W fast wireless charging pad for phone and earbuds simultaneously.", 2999.00, 30, "Electronics", "https://images.unsplash.com/photo-1622445262465-2481c4574875?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "USB-C Multi-Port Dock", "9-in-1 USB-C hub with HDMI 4K, ethernet, SD reader, and PD charging.", 7999.00, 12, "Electronics", "https://images.unsplash.com/photo-1547082299-de196ea013d6?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Smart RGB LED Strip", "Wi-Fi smart light strip compatible with Alexa and Google Assistant.", 1999.00, 45, "Electronics", "https://images.unsplash.com/photo-1565814329452-e1efa11c5b89?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Portable Bluetooth Speaker", "Waterproof IPX7 outdoor speaker with deep bass and 12-hour playtime.", 4999.00, 15, "Electronics", "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "MagSafe Desk Phone Stand", "Aluminum alloy desktop phone holder with magnetic attachment.", 1499.00, 40, "Electronics", "https://images.unsplash.com/photo-1586105251261-72a756497a11?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Keycap Puller & Brush Set", "2-in-1 tool for removing mechanical keycaps and cleaning keyboards.", 499.00, 100, "Electronics", "https://images.unsplash.com/photo-1595225476474-87563907a212?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "RGB Extended Desk Mat", "Large cloth mousepad with illuminated borders and micro-weave texture.", 1899.00, 25, "Electronics", "https://images.unsplash.com/photo-1632292224971-0d45778bd364?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Wireless Earbuds BassPro", "True wireless earbuds with touch control and low latency gaming mode.", 5999.00, 20, "Electronics", "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Portable SSD (1TB)", "Ultra-fast read/write external SSD drive, shockproof aluminum casing.", 9999.00, 8, "Electronics", "https://images.unsplash.com/photo-1597849202758-c0b7baebfdfd?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "RGB Numeric Numpad", "Mechanical external 21-key keypad for bookkeeping and gaming.", 2499.00, 15, "Electronics", "https://images.unsplash.com/photo-1601445638532-3c6f6c3aa1d6?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Adjustable Desktop Ring Light", "10-inch ringlight with tripod stand and phone mount for streaming.", 1799.00, 35, "Electronics", "https://images.unsplash.com/photo-1522202176988-66273c2fd55f?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Smart Voice Assistant Speaker", "Compact smart home speaker with voice command recognition.", 3499.00, 28, "Electronics", "https://images.unsplash.com/photo-1543510473-ac2c35329a28?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Braided HDMI 2.1 Cable", "8K 60Hz high-speed nylon braided cable for monitors and consoles.", 999.00, 60, "Electronics", "https://images.unsplash.com/photo-1557063673-0493e05d49ef?q=80&w=600&auto=format&fit=crop");

                // CATEGORY: Furniture (16 items)
                addProduct(repo, "Ergonomic Mesh Office Chair", "Premium mesh task chair with multi-dimensional lumbar support.", 18999.00, 8, "Furniture", "https://images.unsplash.com/photo-1592078615290-033ee584e267?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Minimalist Wooden Desk", "Solid oak writing desk with built-in cable management channel.", 14499.00, 6, "Furniture", "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Dual-Motor Standing Desk", "Motorized sit-stand desk frame with memory controller preset.", 29999.00, 5, "Furniture", "https://images.unsplash.com/photo-1595515106969-1ce29566ff1c?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Adjustable Laptop Stand", "Ergonomic aluminum cooling riser for laptops up to 17 inches.", 2499.00, 40, "Furniture", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Felt Desk Pad (Large)", "Premium wool felt desk cover protect surface, dampens noise.", 1799.00, 30, "Furniture", "https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Solid Oak Monitor Riser", "Handcrafted monitor stand shelf to raise screen eye-level.", 3499.00, 15, "Furniture", "https://images.unsplash.com/photo-1505797149-43b0069ec26b?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Magnetic Cable Organizer Dock", "Desktop magnetic block to secure charging cords on the desk.", 999.00, 50, "Furniture", "https://images.unsplash.com/photo-1542546068979-b6affb46ea8f?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Leather Fountain Pen Sleeve", "Genuine single pen holder sleeve protect writing instruments.", 799.00, 45, "Furniture", "https://images.unsplash.com/photo-1583485088034-697b5bc54ccd?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Memory Foam Wrist Rest", "Ergonomic keyboard wrist pillow reduce arm and wrist fatigue.", 1299.00, 24, "Furniture", "https://images.unsplash.com/photo-1587829741301-dc798b83add3?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Under-Desk Headphone Hanger", "Universal iron mount clamp for headphones storage under desk.", 899.00, 30, "Furniture", "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Vertical Laptop Stand", "Space saving vertical dock stand adjustable slots for two laptops.", 1999.00, 15, "Furniture", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Modular Wooden Drawers", "Desk side wooden organizer storage cabinet with drawers.", 8999.00, 8, "Furniture", "https://images.unsplash.com/photo-1595428774223-ef52624120d2?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Desk Lamp with Wireless Charger", "Modern LED desk light dimmable levels, touch control, USB charger.", 3999.00, 18, "Furniture", "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Cable Management Sleeves", "Flexible neoprene cord concealment wraps zipper lock mechanism.", 599.00, 80, "Furniture", "https://images.unsplash.com/photo-1557063673-0493e05d49ef?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Anti-Fatigue Standing Mat", "Thick foam comfort mat for standing desks reduce leg pressure.", 2799.00, 12, "Furniture", "https://images.unsplash.com/photo-1562564055-71e051d33c19?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Ergonomic Seat Cushion", "Orthopedic memory foam seat cushion for office chairs.", 1899.00, 20, "Furniture", "https://images.unsplash.com/photo-1586105251261-72a756497a11?q=80&w=600&auto=format&fit=crop");

                // CATEGORY: Accessories (15 items)
                addProduct(repo, "Minimalist Leather Backpack", "Water-resistant full-grain leather pack with 16-inch laptop sleeve.", 3999.00, 12, "Accessories", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Premium Leather Wallet", "Slim bifold wallet made from vegetable-tanned leather, RFID blocks.", 1999.00, 30, "Accessories", "https://images.unsplash.com/photo-1627123424574-724758594e93?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Water-Resistant Tech Pouch", "Travel organizer bag for cables, chargers, mouse and adapters.", 1499.00, 45, "Accessories", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Compact Key Organizer", "Secures keys inside pocket sized leather sleeve, stops rattling.", 899.00, 60, "Accessories", "https://images.unsplash.com/photo-1582139329536-e7284fece509?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Hardcover Bullet Journal", "A5 dotted pages notebook with heavy bleed-resistant paper.", 699.00, 75, "Accessories", "https://images.unsplash.com/photo-1531346878377-a5be20888e57?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Insulated Stainless Steel Flask", "Double wall vacuum insulated water bottle keep drinks cold.", 1299.00, 40, "Accessories", "https://images.unsplash.com/photo-1602143407151-7111542de6e8?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Canvas Messenger Bag", "Heavy-duty canvas shoulder bag with magnetic flap closures.", 2999.00, 15, "Accessories", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Leather Passport Cover", "Slim wallet design to hold passport, tickets, and travel cards.", 1199.00, 30, "Accessories", "https://images.unsplash.com/photo-1627123424574-724758594e93?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Screen Cleaning Kit", "Alcohol-free cleaning spray with premium microfiber cloths.", 499.00, 120, "Accessories", "https://images.unsplash.com/photo-1586495777744-4413f21062fa?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Signature Matte Fountain Pen", "Elegant writing pen with fine nib steel point, ink refillable.", 2499.00, 20, "Accessories", "https://images.unsplash.com/photo-1583485088034-697b5bc54ccd?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Minimalist Card Holder", "Metal frame wallet holding 1-12 cards with elastic band.", 799.00, 65, "Accessories", "https://images.unsplash.com/photo-1627123424574-724758594e93?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Travel Electronics Binder", "Grid elastic band storage sheet to bind accessories neatly.", 1799.00, 22, "Accessories", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Wool Felt Coasters (Set)", "Set of 6 absorbent circular coasters for cups and mugs.", 699.00, 50, "Accessories", "https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?q=80&w=600&auto=format&fit=crop");
                addProduct(repo, "Leather Luggage Tag", "Durable travel identity tag with privacy flap cover.", 499.00, 90, "Accessories", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?q=80&w=600&auto=format&fit=crop");
                // Seed 50 more products programmatically to hit 101 total items!
                String[] prefixes = {"Quantum", "Nomad", "Classic", "Luxe", "Apex", "Nova", "Stellar", "Vista", "Vanguard", "Horizon"};
                String[] techSuffixes = {"Power Bank", "Charging Dock", "Travel Adapter", "Desk Fan", "Cable Clip", "Screen Protector", "LED Strip", "USB Drive", "Cleaning Gel", "Desk Ring Light"};
                String[] furnitureSuffixes = {"Wood Pen Cup", "Under-Desk Tray", "Desk Mat Pro", "Laptop Wedge", "Cable Channel", "Monitor Arm", "Paper Tray", "Magnetic Holder", "Footrest Cushion", "Lumbar Pillow"};
                String[] accessorySuffixes = {"Passport Wallet", "Leather Keychain", "Desk Coaster", "Notebook Cover", "Card Sleeve", "Canvas Tote", "Luggage Tag", "Writing Pad", "Tech Organizer", "Pencil Case"};

                for (int i = 1; i <= 50; i++) {
                    String name = "";
                    String desc = "";
                    String cat = "";
                    String img = "";
                    double price = 0.0;
                    int stock = (i * 7) % 40 + 10; // 10 to 49
                    
                    if (i % 3 == 0) {
                        cat = "Electronics";
                        name = prefixes[i % 10] + " " + techSuffixes[(i / 3) % 10];
                        desc = "High-performance " + name + " designed for high efficiency and smart modern workspaces.";
                        price = 1000.00 + ((i * 123) % 8000);
                        img = "https://images.unsplash.com/photo-1526738549149-8e07eca6c147?q=80&w=600&auto=format&fit=crop";
                    } else if (i % 3 == 1) {
                        cat = "Furniture";
                        name = prefixes[i % 10] + " " + furnitureSuffixes[(i / 3) % 10];
                        desc = "Ergonomic, handcrafted " + name + " to style and organize your modern desktop workspace setup.";
                        price = 800.00 + ((i * 147) % 12000);
                        img = "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?q=80&w=600&auto=format&fit=crop";
                    } else {
                        cat = "Accessories";
                        name = prefixes[i % 10] + " " + accessorySuffixes[(i / 3) % 10];
                        desc = "A premium daily carry " + name + " crafted with premium durable materials for design enthusiasts.";
                        price = 499.00 + ((i * 189) % 3500);
                        img = "https://images.unsplash.com/photo-1627123424574-724758594e93?q=80&w=600&auto=format&fit=crop";
                    }
                    addProduct(repo, name, desc, price, stock, cat, img);
                }

                System.out.println("Seeded 101 high-quality products successfully!");
            }
        };
    }
}
