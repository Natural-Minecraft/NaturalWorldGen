package id.naturalsmp.nwg.toolbelt.format

import id.naturalsmp.nwg.NaturalGenerator
import id.naturalsmp.nwg.toolbelt.plugin.NaturalDevSender
import net.kyori.adventure.text.minimessage.MiniMessage
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.apache.commons.lang.Validate
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.DyeColor
import java.util.*
import java.util.regex.Pattern

/**
 * Colors
 *
 * @author cyberpwn
 */
enum class C(
    private val token: String,
    private val code: Char,
    private val intCode: Int,
    private val isFormat: Boolean = false
) {
    BLACK("^", '0', 0x00) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.BLACK
    },
    DARK_BLUE("^", '1', 0x01) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.DARK_BLUE
    },
    DARK_GREEN("^", '2', 0x02) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.DARK_GREEN
    },
    DARK_AQUA("^", '3', 0x03) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.DARK_AQUA
    },
    DARK_RED("^", '4', 0x04) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.DARK_RED
    },
    DARK_PURPLE("^", '5', 0x05) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.DARK_PURPLE
    },
    GOLD("^", '6', 0x06) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.GOLD
    },
    GRAY("^", '7', 0x07) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.GRAY
    },
    DARK_GRAY("^", '8', 0x08) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.DARK_GRAY
    },
    BLUE("^", '9', 0x09) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.BLUE
    },
    GREEN("^", 'a', 0x0A) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.GREEN
    },
    IRIS("<#1bb19e>", 'a', 0x0A) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.GREEN
    },
    AQUA("^", 'b', 0x0B) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.AQUA
    },
    RED("^", 'c', 0x0C) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.RED
    },
    LIGHT_PURPLE("^", 'd', 0x0D) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.LIGHT_PURPLE
    },
    YELLOW("^", 'e', 0x0E) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.YELLOW
    },
    WHITE("^", 'f', 0x0F) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.WHITE
    },
    MAGIC("<obf>", 'k', 0x10, true) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.MAGIC
    },
    BOLD("^", 'l', 0x11, true) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.BOLD
    },
    STRIKETHROUGH("^", 'm', 0x12, true) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.STRIKETHROUGH
    },
    UNDERLINE("<underlined>", 'n', 0x13, true) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.UNDERLINE
    },
    ITALIC("^", 'o', 0x14, true) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.ITALIC
    },
    RESET("^", 'r', 0x15) {
        override fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.RESET
    };

    val actualToken: String get() = if (token == "^") "<${name.lowercase(Locale.ROOT)}>" else token
    val asString: String get() = "$COLOR_CHAR$code"

    open fun asBungee(): net.md_5.bungee.api.ChatColor = net.md_5.bungee.api.ChatColor.RESET

    override fun toString(): String = if (intCode == -1) actualToken else asString

    fun getChar(): Char = code

    fun dye(): DyeColor = chatToDye(chatColor())

    fun hex(): String = chatToHex(this)

    fun awtColor(): java.awt.Color = java.awt.Color.decode(hex())

    fun isFormat(): Boolean = isFinishFormat() || isFormat

    fun isColor(): Boolean = !isFormat() && this != RESET

    fun chatColor(): ChatColor = ChatColor.getByChar(code)!!

    private fun isFinishFormat(): Boolean = isFormat

    fun getMeta(): Byte = when (this) {
        AQUA -> 11
        BLACK -> 0
        BLUE, DARK_AQUA -> 9
        DARK_BLUE -> 1
        DARK_GRAY -> 8
        DARK_GREEN -> 2
        DARK_PURPLE -> 5
        DARK_RED -> 4
        GOLD -> 6
        GRAY -> 7
        GREEN -> 10
        LIGHT_PURPLE -> 13
        RED -> 12
        YELLOW -> 14
        else -> 15
    }

    fun getItemMeta(): Byte = when (this) {
        AQUA, DARK_AQUA -> 9
        BLUE -> 3
        DARK_BLUE -> 11
        DARK_GRAY -> 7
        DARK_GREEN -> 13
        DARK_PURPLE -> 10
        DARK_RED, RED -> 14
        GOLD, YELLOW -> 4
        GRAY -> 8
        GREEN -> 5
        LIGHT_PURPLE -> 2
        WHITE -> 0
        else -> 15
    }

    companion object {
        const val COLOR_CHAR = '\u00A7'
        val COLORCYCLE = arrayOf(GOLD, YELLOW, GREEN, AQUA, LIGHT_PURPLE, AQUA, GREEN, YELLOW, GOLD, RED)
        private val STRIP_COLOR_PATTERN = Pattern.compile("(?i)$COLOR_CHAR[0-9A-FK-OR]")
        private val COLORS = arrayOf(BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE)
        private val BY_ID = mutableMapOf<Int, C>()
        private val BY_CHAR = mutableMapOf<Char, C>()
        private val dyeChatMap = mutableMapOf<DyeColor, C>()
        private val chatHexMap = mutableMapOf<C, String>()
        private val dyeHexMap = mutableMapOf<DyeColor, String>()

        init {
            for (color in values()) {
                BY_ID[color.intCode] = color
                BY_CHAR[color.code] = color
            }
            chatHexMap[BLACK] = "#000000"
            chatHexMap[DARK_BLUE] = "#0000AA"
            chatHexMap[IRIS] = "#1bb19e"
            chatHexMap[DARK_GREEN] = "#00AA00"
            chatHexMap[DARK_AQUA] = "#00AAAA"
            chatHexMap[DARK_RED] = "#AA0000"
            chatHexMap[DARK_PURPLE] = "#AA00AA"
            chatHexMap[GOLD] = "#FFAA00"
            chatHexMap[GRAY] = "#AAAAAA"
            chatHexMap[DARK_GRAY] = "#555555"
            chatHexMap[BLUE] = "#5555FF"
            chatHexMap[GREEN] = "#55FF55"
            chatHexMap[AQUA] = "#55FFFF"
            chatHexMap[RED] = "#FF5555"
            chatHexMap[LIGHT_PURPLE] = "#FF55FF"
            chatHexMap[YELLOW] = "#FFFF55"
            chatHexMap[WHITE] = "#FFFFFF"
            dyeChatMap[DyeColor.BLACK] = DARK_GRAY
            dyeChatMap[DyeColor.BLUE] = DARK_BLUE
            dyeChatMap[DyeColor.BROWN] = GOLD
            dyeChatMap[DyeColor.CYAN] = AQUA
            dyeChatMap[DyeColor.GRAY] = GRAY
            dyeChatMap[DyeColor.GREEN] = DARK_GREEN
            dyeChatMap[DyeColor.LIGHT_BLUE] = BLUE
            dyeChatMap[DyeColor.LIME] = GREEN
            dyeChatMap[DyeColor.MAGENTA] = LIGHT_PURPLE
            dyeChatMap[DyeColor.ORANGE] = GOLD
            dyeChatMap[DyeColor.PINK] = LIGHT_PURPLE
            dyeChatMap[DyeColor.PURPLE] = DARK_PURPLE
            dyeChatMap[DyeColor.RED] = RED
            dyeChatMap[DyeColor.LIGHT_GRAY] = GRAY
            dyeChatMap[DyeColor.WHITE] = WHITE
            dyeChatMap[DyeColor.YELLOW] = YELLOW
            dyeHexMap[DyeColor.BLACK] = "#181414"
            dyeHexMap[DyeColor.BLUE] = "#253193"
            dyeHexMap[DyeColor.BROWN] = "#56331c"
            dyeHexMap[DyeColor.CYAN] = "#267191"
            dyeHexMap[DyeColor.GRAY] = "#414141"
            dyeHexMap[DyeColor.GREEN] = "#364b18"
            dyeHexMap[DyeColor.LIGHT_BLUE] = "#6387d2"
            dyeHexMap[DyeColor.LIME] = "#39ba2e"
            dyeHexMap[DyeColor.MAGENTA] = "#be49c9"
            dyeHexMap[DyeColor.ORANGE] = "#ea7e35"
            dyeHexMap[DyeColor.PINK] = "#d98199"
            dyeHexMap[DyeColor.PURPLE] = "#7e34bf"
            dyeHexMap[DyeColor.RED] = "#9e2b27"
            dyeHexMap[DyeColor.LIGHT_GRAY] = "#a0a7a7"
            dyeHexMap[DyeColor.WHITE] = "#a4a4a4"
            dyeHexMap[DyeColor.YELLOW] = "#c2b51c"
        }

        @JvmStatic
        fun spin(c: FloatArray, shift: Int): FloatArray = floatArrayOf(spin(c[0], shift), spinc(c[1], shift), spinc(c[2], shift))

        @JvmStatic
        fun spin(c: FloatArray, a: Int, b: Int, d: Int): FloatArray = floatArrayOf(spin(c[0], a), spinc(c[1], b), spinc(c[2], d))

        @JvmStatic
        fun spin(c: Float, shift: Int): Float {
            val g = (Math.floor((c * 360).toDouble()).toInt() + shift) % 360 / 360f
            return if (g < 0) 1f - g else g
        }

        @JvmStatic
        fun spinc(c: Float, shift: Int): Float {
            val g = (Math.floor((c * 255).toDouble()).toInt() + shift) / 255f
            return Math.max(0f, Math.min(g, 1f))
        }

        @JvmStatic
        fun spin(c: java.awt.Color, h: Int, s: Int, b: Int): java.awt.Color {
            var hsb = java.awt.Color.RGBtoHSB(c.red, c.green, c.blue, null)
            hsb = spin(hsb, h, s, b)
            return java.awt.Color.getHSBColor(hsb[0], hsb[1], hsb[2])
        }

        @JvmStatic
        fun spinToHex(color: C, h: Int, s: Int, b: Int): String = "#" + Integer.toHexString(spin(color.awtColor(), h, s, b).rgb).substring(2)

        @JvmStatic
        fun mini(s: String): String {
            val msg = compress(s)
            val b = StringBuilder()
            var c = false
            for (i in msg.toCharArray()) {
                if (!c) {
                    if (i == COLOR_CHAR) {
                        c = true
                        continue
                    }
                    b.append(i)
                } else {
                    c = false
                    val o = getByChar(i)
                    b.append(o.actualToken)
                }
            }
            return b.toString()
        }

        @JvmStatic
        @JvmOverloads
        fun aura(s: String, hrad: Int, srad: Int, vrad: Int, pulse: Double = 0.3): String {
            val msg = compress(s)
            val b = StringBuilder()
            var c = false
            for (i in msg.toCharArray()) {
                if (c) {
                    c = false
                    val o = getByChar(i)
                    if (hrad != 0 || srad != 0 || vrad != 0) {
                        if (pulse > 0) {
                            b.append(NaturalDevSender.pulse(spinToHex(o, hrad, srad, vrad), spinToHex(o, -hrad, -srad, -vrad), pulse))
                        } else {
                            b.append("<gradient:")
                                .append(spinToHex(o, hrad, srad, vrad))
                                .append(":")
                                .append(spinToHex(o, -hrad, -srad, -vrad))
                                .append(">")
                        }
                    } else {
                        b.append(getByChar(i).actualToken)
                    }
                    continue
                }
                if (i == COLOR_CHAR) {
                    c = true
                    continue
                }
                b.append(i)
            }
            return b.toString()
        }

        @JvmStatic
        fun compress(c: String): String = BaseComponent.toLegacyText(*TextComponent.fromLegacyText(c))

        @JvmStatic
        fun getByChar(code: Char): C {
            return try {
                BY_CHAR[code] ?: WHITE
            } catch (e: Exception) {
                NaturalGenerator.reportError(e)
                WHITE
            }
        }

        @JvmStatic
        fun getByChar(code: String): C {
            return try {
                Validate.notNull(code, "Code cannot be null")
                Validate.isTrue(code.isNotEmpty(), "Code must have at least one char")
                BY_CHAR[code[0]] ?: WHITE
            } catch (e: Exception) {
                NaturalGenerator.reportError(e)
                WHITE
            }
        }

        @JvmStatic
        fun stripColor(input: String?): String? {
            return input?.let { STRIP_COLOR_PATTERN.matcher(it).replaceAll("") }
        }

        @JvmStatic
        fun strip(input: String?): String? {
            return input?.let { MiniMessage.miniMessage().stripTags(stripColor(it)!!) }
        }

        @JvmStatic
        fun dyeToChat(dclr: DyeColor): C = dyeChatMap[dclr] ?: MAGIC

        @JvmStatic
        fun chatToDye(color: ChatColor): DyeColor {
            for ((key, value) in dyeChatMap) {
                if (value.toString() == color.toString()) return key
            }
            return DyeColor.BLACK
        }

        @JvmStatic
        fun chatToHex(clr: C): String = chatHexMap[clr] ?: "#000000"

        @JvmStatic
        fun dyeToHex(clr: DyeColor): String = dyeHexMap[clr] ?: "#000000"

        @JvmStatic
        fun hexToColor(hexStr: String): Color? {
            var hex = hexStr
            if (hex.startsWith("#")) hex = hex.substring(1)
            if (hex.contains("x")) hex = hex.substring(hex.indexOf("x"))
            if (hex.length != 6 && hex.length != 3) return null
            val sz = hex.length / 3
            val mult = 1 shl (2 - sz) * 4
            var x = 0
            var i = 0
            var z = 0
            while (z < hex.length) {
                x = x or (mult * Integer.parseInt(hex.substring(z, z + sz), 16) shl i * 8)
                ++i
                z += sz
            }
            return Color.fromBGR(x and 0xffffff)
        }

        @JvmStatic
        fun rgbToColor(rgb: String): Color? {
            val parts = rgb.split("[^0-9]+".toRegex()).toTypedArray()
            if (parts.size < 3) return null
            var x = 0
            for (i in 0..2) {
                x = x or (Integer.parseInt(parts[i]) shl i * 8)
            }
            return Color.fromBGR(x and 0xffffff)
        }

        @JvmStatic
        fun generateColorTable(): String {
            val str = StringBuilder()
            str.append("<table><tr><td>Chat Color</td><td>Color</td></tr>")
            for ((key, value) in chatHexMap) {
                str.append(String.format("<tr><td style='color: %2\$s;'>%1\$s</td><td style='color: %2\$s;'>Test String</td></tr>", key.name, value))
            }
            str.append("</table>")
            str.append("<table><tr><td>Dye Color</td><td>Color</td></tr>")
            for ((key, value) in dyeHexMap) {
                str.append(String.format("<tr><td style='color: %2\$s;'>%1\$s</td><td style='color: %2\$s;'>Test String</td></tr>", key.name, value))
            }
            str.append("</table>")
            return str.toString()
        }

        @JvmStatic
        fun translateAlternateColorCodes(altColorChar: Char, textToTranslate: String?): String? {
            if (textToTranslate == null) return null
            val b = textToTranslate.toCharArray()
            for (i in 0 until b.size - 1) {
                if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                    b[i] = COLOR_CHAR
                    b[i + 1] = b[i + 1].lowercaseChar()
                }
            }
            return String(b)
        }

        @JvmStatic
        fun fromItemMeta(c: Byte): C? {
            for (i in values()) {
                if (i.getItemMeta() == c) return i
            }
            return null
        }

        @JvmStatic
        fun randomColor(): C = COLORS[(Math.random() * (COLORS.size - 1)).toInt()]

        @JvmStatic
        fun getLastColors(input: String): String {
            val result = StringBuilder()
            val length = input.length
            for (index in length - 1 downTo 0) {
                val section = input[index]
                if (section == COLOR_CHAR && index < length - 1) {
                    val c = input[index + 1]
                    val color = getByChar(c)
                    result.insert(0, color)
                    if (color.isColor() || color == RESET) break
                }
            }
            return result.toString()
        }
    }
}
