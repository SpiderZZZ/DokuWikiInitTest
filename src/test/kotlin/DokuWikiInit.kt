import com.codeborne.selenide.CollectionCondition.size
import com.codeborne.selenide.Condition.*
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.confirm
import com.codeborne.selenide.Selenide.open
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.By
import java.io.File
import kotlin.test.assertEquals

class DokuWikiInit {

/*
1. Открыть докувики по адресу http://localhost:80/
2. Зайти в админскую часть системы
3. Перейти в меню Admin -> User Manager
4. Удалить пользователей, если есть
5. Импортировать файл users.csv
6. Убедится, что все пользователи появились
7. Установить всем пользователям пароль "1"
*/

    var mainPageAddress = "http://localhost:80/"
    var login = "superuser"
    var password = "bitnami1"

    @Before fun setUp() {
        open(mainPageAddress)
        assertEquals("start [Bitnami DokuWiki]",Selenide.title())
        Selenide.`$`(By.xpath("//a[@title='Log In']")).click()
        Selenide.`$`(By.name("u")).sendKeys(login)
        Selenide.`$`(By.name("p")).sendKeys(password)
        Selenide.`$`(By.xpath("//button[text()='Log In']")).click()
        Selenide.`$`(By.xpath("//a[@title='Admin']")).click()
    }

    @Test fun deleteExistingUsers() {
        Selenide.`$`(By.xpath("//a[@title='Admin']")).click()
        Selenide.`$`(By.xpath("//span[text()='User Manager']")).click()
        val users = Selenide.`$$`(By.xpath("//div[@class='table']//table[@class='inline']//input[contains(@name,'test')]"))
        users.forEach()
        {
            it.click()
        }
        Selenide.`$`(By.name("fn[delete]")).click()
        confirm("Really delete selected item(s)?")
    }

    @Test fun uploadNewUsers() {
        Selenide.`$`(By.xpath("//a[@title='Admin']")).click()
        Selenide.`$`(By.xpath("//span[text()='User Manager']")).click()
        var file = File("users.csv")
        Selenide.`$`(By.name("import")).uploadFile(file)
        Selenide.`$`(By.name("fn[import]")).click()
        Selenide.`$`(By.xpath("//div[@class='success']")).shouldHave(text("User Import: 9 users found, 9 imported successfully."))

        val users = Selenide.`$$`(By.xpath("//div[@class='table']//table[@class='inline']//a[contains(text(),'test')]")).shouldHave(size(9))
        users.forEach()
        {
            it.click()
            Selenide.`$`(By.id("modify_userpass")).sendKeys("1")
            Selenide.`$`(By.id("modify_userpass2")).sendKeys("1")
            Selenide.`$`(By.name("fn[modify]")).click()
        }
    }

    @After fun logout() {
        Selenide.`$`(By.xpath("//a[@title='Log Out']")).click()
    }
}