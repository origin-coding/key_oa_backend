package oa.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import oa.annotation.Desensitize
import jakarta.persistence.*
import java.util.*
import kotlin.collections.HashSet

@Entity
class Employee(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long?,

    @Column(nullable = false) var name: String,

    @Column(nullable = false) var gender: Boolean,  // 性别，true：男；false：女

    @Desensitize(strategy = Desensitize.DesensitizeStrategy.PHONE)
    @Column(nullable = false, unique = true) var phone: String,

    @Column(nullable = false, unique = true) var email: String,

    @Desensitize(strategy = Desensitize.DesensitizeStrategy.ID_CARD)
    @Column(nullable = false, unique = true) var identity: String,

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false) var birthday: Date,

    @Column(nullable = false, unique = true) var jobNumber: String,  // 员工的工号

    @Desensitize(strategy = Desensitize.DesensitizeStrategy.PASSWORD)
    @Column(nullable = false) var password: String,

    @Column(nullable = false) var activated: Boolean,  // 账号是否激活，未激活不允许使用

    @JsonIgnore
    @ManyToMany(mappedBy = "employees", cascade = [CascadeType.ALL])
    var roles: MutableSet<Role> = HashSet()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Employee) return false

        if (id != other.id) return false
        if (identity != other.identity) return false
        if (jobNumber != other.jobNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + identity.hashCode()
        result = 31 * result + jobNumber.hashCode()
        return result
    }
}