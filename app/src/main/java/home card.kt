import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun  Homecard(title: String = "Home", background: Color = Color.White){
    Card(
        modifier = Modifier
            .size(150.dp),
        shape = RoundedCornerShape(10.dp),
        colors = cardColors(
            containerColor = background
        ),
        elevation = cardElevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp
        )
    ) {
        Text(text = title)
    }

}
@Preview(showBackground = true)
@Composable
fun HomecardPreview(){
    Homecard()
}
