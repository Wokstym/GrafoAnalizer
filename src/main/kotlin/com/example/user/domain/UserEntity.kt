package com.example.user.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
class UserEntity() {

    constructor(username: String, twitterId: String, name: String) : this() {
        this.username = username
        this.twitterId = twitterId
        this.nameAndSurname = name
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    lateinit var username: String
    lateinit var nameAndSurname: String
    lateinit var twitterId: String

    @CollectionTable(name = "users_roles", joinColumns = [JoinColumn(name = "user_id")])
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "role")
    var roles: Set<String> = setOf("ROLE_USER")

    @CreatedDate
    lateinit var createdAt: LocalDateTime

    @LastModifiedDate
    lateinit var updatedAt: LocalDateTime
}
