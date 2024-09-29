@file:Suppress("JUnitMalformedDeclaration")

package webapp.users

import arrow.core.Either.Left
import arrow.core.Either.Right
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.Validator
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import webapp.TestUtils.Data.user
import webapp.TestUtils.countRoles
import webapp.TestUtils.countUsers
import webapp.TestUtils.defaultRoles
import webapp.TestUtils.deleteAllUsersOnly
import webapp.core.utils.AppUtils.cleanField
import webapp.core.utils.i
import webapp.users.User.UserDao.Dao.findOneUserByEmail
import webapp.users.User.UserDao.Dao.save
import webapp.users.User.UserDao.Dao.toJson
import kotlin.test.*


@SpringBootTest(properties = ["spring.main.web-application-type=reactive"])
@ActiveProfiles("test")
class UserTests {

    @Autowired
    lateinit var context: ApplicationContext
    val mapper: ObjectMapper by lazy { context.getBean() }
    val validator: Validator by lazy { context.getBean() }

    @AfterTest
    fun cleanUp() = runBlocking { context.deleteAllUsersOnly() }

    @Test
    fun `save default user should work in this context `() = runBlocking {
        val count = context.countUsers()
        (user to context).save()
        assertEquals(expected = count + 1, context.countUsers())
    }

    @Test
    fun `count users, expected 0`() =
        runBlocking {
            assertEquals(
                0,
                context.countUsers(),
                "because init sql script does not inserts default users."
            )
        }

    @Test
    fun `count roles, expected 3`() = runBlocking {
        context.run {
            assertEquals(
                defaultRoles.size,
                countRoles(),
                "Because init sql script does insert default roles."
            )
        }
    }

    @Test
    fun `display user formatted in JSON`() = assertDoesNotThrow {
        (user to context).toJson.let(::i)
    }

    @Test
    fun `check toJson build a valid json format`(): Unit = assertDoesNotThrow {
        (user to context).toJson.let(mapper::readTree)
    }


    @Test
    fun `test cleanField extension function`() = assertEquals(
        "login",
        "`login`".cleanField(),
        "Backtick should be removed"
    )


    @Test
    fun `check findOneByEmail with non-existent email`() = runBlocking {
        (user to context).save()
        assertEquals(1, context.countUsers())
        (user to context).findOneUserByEmail("user@dummy.com").run {
            when (this) {
                is Left -> assertNotNull(value)

                is Right -> assertNull(value)
            }
        }
    }

    @Test
    fun `check findOneByEmail with existant email`() = runBlocking {
        (user to context).save()
        assertEquals(1, context.countUsers())
        (user to context).findOneUserByEmail(user.email).run {
            when (this) {
                is Left -> assertEquals(value::class.java, NullPointerException::class.java)

                is Right -> assertEquals(user, value)
            }
        }
    }
}