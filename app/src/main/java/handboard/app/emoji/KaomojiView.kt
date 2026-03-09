package handboard.app.emoji

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import handboard.app.core.theme.ActionKeyBackground
import handboard.app.core.theme.KeyBackground
import handboard.app.core.theme.KeyText
import handboard.app.core.theme.KeyTextDim
import handboard.app.core.theme.KeyboardBackground
import handboard.app.core.theme.ShiftActiveBackground

data class KaomojiCategory(val name: String, val items: List<String>)

object KaomojiData {
    val categories = listOf(
        KaomojiCategory("Happy", listOf(
            "(◕‿◕)","(｡◕‿◕｡)","(◠‿◠)","(✿◠‿◠)","(◕ᴗ◕✿)","(◠‿◠✿)",
            "(≧◡≦)","(☆▽☆)","(✧ω✧)","(◕‿◕✿)","(✯◡✯)","(◕‿-)","(◠ᴗ◠✿)"
        )),
        KaomojiCategory("Sad", listOf(
            "(╥﹏╥)","(T_T)","(;_;)","(πーπ)","(╯︵╰,)","(´;ω;`)",
            "(｡•́︿•̀｡)","(ಥ_ಥ)","(´°̥̥̥̥̥̥̥̥ω°̥̥̥̥̥̥̥̥`)","(ᗒᗩᗕ)"
        )),
        KaomojiCategory("Love", listOf(
            "(♥‿♥)","(◕‿◕)♡","(◍•ᴗ•◍)❤","(♡˙︶˙♡)","(≧◡≦) ♡","(✿ ♥‿♥)",
            "(´∀`)♡","♡(ŐωŐ人)","(♡ >ω< ♡)","(◕‿◕)♥"
        )),
        KaomojiCategory("Angry", listOf(
            "(╬ Ò﹏Ó)","(ノಠ益ಠ)ノ彡┻━┻","(╯°□°)╯︵ ┻━┻","(ಠ_ಠ)",
            "(¬_¬)","(ᗒᗣᗕ)","(눈_눈)","ಠ_ಠ","(>_<)","(≧σ≦)"
        )),
        KaomojiCategory("Surprise", listOf(
            "(⊙_⊙)","(°o°)","(O_O)","Σ(°△°|||)","(⊙ˍ⊙)","(°ロ°)",
            "(゜o゜)","(*⁰▿⁰*)","(◎_◎)","w(°o°)w"
        )),
        KaomojiCategory("Actions", listOf(
            "¯\\_(ツ)_/¯","(づ◕‿◕)づ","(つ≧▽≦)つ","(ﾉ◕ヮ◕)ﾉ*:・ﾟ✧",
            "( ˘▽˘)っ♨","(◕ᴗ◕✿)","ᕙ(⇀‸↼‶)ᕗ","(^-^)/","(ノ^_^)ノ",
            "ヽ(>∀<☆)ノ","┬─┬ノ( º _ ºノ)","(☞ﾟヮﾟ)☞"
        )),
        KaomojiCategory("Animals", listOf(
            "(=^・ω・^=)","(=^‥^=)","ʕ•ᴥ•ʔ","(・⊝・)","U・ᴥ・U",
            "(≧ω≦)","(◕ᴥ◕)","ʕ·ᴥ·ʔ","( ̄(工) ̄)","₍˄·͈༝·͈˄₎"
        )),
        KaomojiCategory("Text Art", listOf(
            "★","☆","♪","♫","♬","✿","❀","❁","☀","☁",
            "❄","♨","⚡","✦","✧","◈","◆","▣","▤","◐"
        ))
    )
}

@Composable
fun KaomojiView(
    heightScale: Float = 1f,
    onKaomojiClick: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var selectedCategory by remember { mutableIntStateOf(0) }
    val panelHeight = (220 * heightScale).dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(KeyboardBackground)
    ) {
        // Category tabs
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(KaomojiData.categories) { index, cat ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (index == selectedCategory) ShiftActiveBackground else ActionKeyBackground)
                        .clickable { selectedCategory = index }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = cat.name, color = KeyText, fontSize = 12.sp)
                }
            }
        }

        // Kaomoji grid
        val kaomojis = KaomojiData.categories.getOrNull(selectedCategory)?.items ?: emptyList()

        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(panelHeight),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(kaomojis) { kaomoji ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(KeyBackground)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onKaomojiClick(kaomoji)
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = kaomoji,
                        color = KeyText,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
