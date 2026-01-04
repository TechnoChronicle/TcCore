package net.technochronicle.tccore.api.lang

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.technochronicle.tccore.api.annotation.TranslationKeyProvider

import com.google.common.base.Supplier

import kotlin.also
import kotlin.apply
import kotlin.collections.forEach
import kotlin.collections.indices
import kotlin.collections.map
import kotlin.collections.toMutableList
import kotlin.collections.toTypedArray
import kotlin.text.isNotEmpty
import kotlin.text.lines
import kotlin.toString

class ComponentListSupplier(var list: MutableList<ComponentSupplier> = mutableListOf()) : Supplier<List<Component>> {
    var translationPrefix: String = ""
        private set
    var line: Int = 0

    override fun get(): List<Component> {
        val result = list.map { it.get() }
        return result
    }
    fun getSupplier(): Supplier<List<Component>> = this
    fun getArray(): Array<Component> = get().toTypedArray()

    fun add(component: ComponentSupplier, style: ComponentSupplier.() -> ComponentSupplier = { this }) {
        val styledComponent = style(component)
        list.add(styledComponent)
        line += 1
    }
    fun add(other: ComponentListSupplier) {
        list.addAll(other.list)
    }
    fun add(other: ComponentListSupplier, style: ComponentSupplier.() -> ComponentSupplier = { this }) {
        for (supplier in other.list) {
            add(supplier, style)
        }
    }

    fun apply(tooltips: MutableList<Component>) {
        tooltips.addAll(get())
    }

    // ////////////////////////////////
    // ****** 翻译前缀 ******//
    // //////////////////////////////
    fun setTranslationPrefix(prefix: String) {
        this.translationPrefix = prefix
    }
}
fun ComponentListSupplier(op: ComponentListSupplier.() -> Unit): ComponentListSupplier {
    val supplier = ComponentListSupplier()
    supplier.op()
    supplier.initialize()
    return supplier
}

class ComponentSupplier(var component: Component, private val delayed: MutableList<(MutableComponent) -> Unit> = mutableListOf(), private val transform: MutableList<(ComponentSupplier) -> ComponentSupplier> = mutableListOf()) : Supplier<Component> {
    override fun get(): MutableComponent {
        var supplier = this
        // 先应用所有的transform变换
        transform.forEach { transformation ->
            supplier = transformation(supplier)
        }
        val result = supplier.component.copy()
        // 再应用所有的delayed操作
        supplier.delayed.forEach { it(result) }
        return result
    }
    fun apply(tooltips: MutableList<Component>) {
        tooltips.add(get())
    }

    operator fun plus(other: ComponentSupplier): ComponentSupplier {
        val newSupplier = ComponentSupplier(component, delayed.toMutableList(), transform.toMutableList())
        newSupplier.delayed.add { result ->
            result.append(other.get())
        }
        return newSupplier
    }

    private fun operatorComponent(op: MutableComponent.() -> Unit): ComponentSupplier {
        val newSupplier = ComponentSupplier(component, delayed.toMutableList(), transform.toMutableList())
        newSupplier.delayed.add { result ->
            op.invoke(result)
        }
        return newSupplier
    }
    private fun transformComponent(trans: (ComponentSupplier) -> ComponentSupplier): ComponentSupplier {
        val newSupplier = ComponentSupplier(component, delayed.toMutableList(), transform.toMutableList())
        newSupplier.transform.add(trans)
        return newSupplier
    }

    // ////////////////////////////////
    // ****** 颜色 ******//
    // //////////////////////////////
    fun gray(): ComponentSupplier = operatorComponent {
        withStyle { it.withColor(ChatFormatting.GRAY) }
    }

    fun white(): ComponentSupplier = operatorComponent {
        withStyle { it.withColor(ChatFormatting.WHITE) }
    }

    fun gold(): ComponentSupplier = operatorComponent {
        withStyle { it.withColor(ChatFormatting.GOLD) }
    }

    fun red(): ComponentSupplier = operatorComponent {
        withStyle { it.withColor(ChatFormatting.RED) }
    }

    fun yellow(): ComponentSupplier = operatorComponent {
        withStyle { it.withColor(ChatFormatting.YELLOW) }
    }

    fun green(): ComponentSupplier = operatorComponent {
        withStyle { it.withColor(ChatFormatting.GREEN) }
    }

    fun orange(): ComponentSupplier = operatorComponent {
        withStyle { it.withColor(0xFFA500) }
    }

    fun aqua(): ComponentSupplier = operatorComponent {
        withStyle { it.withColor(ChatFormatting.AQUA) }
    }

    fun lightPurple(): ComponentSupplier = operatorComponent {
        withStyle { it.withColor(ChatFormatting.LIGHT_PURPLE) }
    }

    fun blue(): ComponentSupplier = operatorComponent {
        withStyle { it.withColor(ChatFormatting.BLUE) }
    }

    fun color(int: Int): ComponentSupplier = operatorComponent {
        withStyle { it.withColor(int) }
    }

    // ////////////////////////////////
    // ****** 格式 ******//
    // //////////////////////////////
    fun italic(): ComponentSupplier {
        operatorComponent { withStyle(ChatFormatting.ITALIC) }
        return this
    }
    fun bold(): ComponentSupplier {
        operatorComponent { withStyle(ChatFormatting.BOLD) }
        return this
    }
    fun underline(): ComponentSupplier {
        operatorComponent { withStyle(ChatFormatting.UNDERLINE) }
        return this
    }
    fun strikethrough(): ComponentSupplier {
        operatorComponent { withStyle(ChatFormatting.STRIKETHROUGH) }
        return this
    }
    fun obfuscated(): ComponentSupplier {
        operatorComponent { withStyle(ChatFormatting.OBFUSCATED) }
        return this
    }
    fun reset(): ComponentSupplier {
        operatorComponent { withStyle(ChatFormatting.RESET) }
        return this
    }
}
fun Component.toComponentSupplier() = ComponentSupplier(this.copy())
fun <T> T.toLiteralSupplier() = (Component.literal(this.toString())).toComponentSupplier()
infix fun String.translatedTo(other: String): ComponentSupplier {
    val translationKey = TranslationKeyProvider.getTranslationKey(this, other)
    return Component.translatable(translationKey).toComponentSupplier()
}

@JvmName("initialize")
fun ComponentSupplier.initialize(): ComponentSupplier = this.also { it.get() }

@JvmName("initializeList")
fun ComponentListSupplier.initialize(): ComponentListSupplier = this.also { it.get() }

@JvmName("initializeFloat")
fun ((Float) -> ComponentSupplier).initialize(): (Float) -> ComponentSupplier = this.also { it(0f) }

@JvmName("initializeInt")
fun ((Int) -> ComponentSupplier).initialize(): (Int) -> ComponentSupplier = this.also { it(0) }

@JvmName("initializeListInt")
fun ((Int) -> ComponentListSupplier).initialize(): (Int) -> ComponentListSupplier = this.also { it(0) }

@JvmName("initializeBoolean")
fun ((Boolean) -> ComponentSupplier).initialize(): (Boolean) -> ComponentSupplier = this.also { it(false) }

@JvmName("initializeString")
fun ((String) -> ComponentSupplier).initialize(): (String) -> ComponentSupplier = this.also { it("") }

@JvmName("initializeLong")
fun ((Long) -> ComponentSupplier).initialize(): (Long) -> ComponentSupplier = this.also { it(0L) }

@JvmName("initializeItem")
fun ((ComponentSupplier) -> ComponentSupplier).initialize(): (ComponentSupplier) -> ComponentSupplier = this.also { it("".toLiteralSupplier()) }

@JvmName("initializeListItem")
fun ((ComponentSupplier) -> ComponentListSupplier).initialize(): (ComponentSupplier) -> ComponentListSupplier = this.also { it("".toLiteralSupplier()) }

@JvmName("initializeListFloat")
fun ((Float) -> ComponentListSupplier).initialize(): (Float) -> ComponentListSupplier = this.also { it(0f) }

@JvmName("initializeListString")
fun ((String) -> ComponentListSupplier).initialize(): (String) -> ComponentListSupplier = this.also { it("") }

@JvmName("initializeListString2")
fun ((String, String) -> ComponentListSupplier).initialize(): (String, String) -> ComponentListSupplier = this.also { it("", "") }
