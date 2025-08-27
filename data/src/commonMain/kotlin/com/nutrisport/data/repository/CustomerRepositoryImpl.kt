package com.nutrisport.data.repository

import com.mmk.nutrisport.util.RequestState
import com.nutrisport.domain.model.Customer
import com.nutrisport.domain.repository.CustomerRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class CustomerRepositoryImpl : CustomerRepository {
    override fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun createCustomer(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        try {
            if (user != null) {
                val customerCollection = Firebase.firestore.collection(collectionPath = "customer")
                val customer = Customer(
                    id = user.uid,
                    firstName = user.displayName?.split(" ")?.firstOrNull() ?: "Unknown",
                    lastName = user.displayName?.split(" ")?.lastOrNull() ?: "Unknown",
                    email = user.email ?: "Unknown",
                )

                val customerExists = customerCollection.document(user.uid).get().exists

                if (customerExists) {
                    onSuccess()
                } else {
                    customerCollection.document(user.uid)
                        .set(customer)
                    customerCollection.document(user.uid)
                        .collection("privateData")
                        .document("role")
                        .set(mapOf("isAdmin" to true))
                    onSuccess()
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while creating a Customer: ${e.message}")
        }
    }

    override suspend fun signOut(): RequestState<Unit> {
        return try {
            Firebase.auth.signOut()
            RequestState.Success(data = Unit)
        } catch (e: Exception) {
            RequestState.Error("Error while signing out: ${e.message}")
        }
    }

    override fun readCustomerFlow(): Flow<RequestState<Customer>> = channelFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            send(RequestState.Error("User is not available."))
            return@channelFlow
        }

        val database = Firebase.firestore

        try {
            database.collection("customer")
                .document(userId)
                .snapshots
                .collectLatest { document ->
                    if (document.exists) {
                        val customer = Customer(
                            id = document.id,
                            firstName = document.get("firstName"),
                            lastName = document.get("lastName"),
                            email = document.get("email"),
                            city = document.get("city"),
                            postalCode = document.get("postalCode"),
                            address = document.get("address"),
                            phoneNumber = document.get("phoneNumber"),
                            cart = document.get("cart"),
                            isAdmin = document.get("isAdmin")
                        )
                        send(RequestState.Success(customer))
                    } else {
                        send(RequestState.Error("Customer document does not exist."))
                    }
                }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading customer info: ${e.message}"))
        }
    }


    override suspend fun updateCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val firestore = Firebase.firestore
                val customerCollection = firestore.collection(collectionPath = "customer")

                val existingCustomer = customerCollection
                    .document(customer.id)
                    .get()
                if (existingCustomer.exists) {
                    customerCollection
                        .document(customer.id)
                        .update(
                            "firstName" to customer.firstName,
                            "lastName" to customer.lastName,
                            "city" to customer.city,
                            "postalCode" to customer.postalCode,
                            "address" to customer.address,
                            "phoneNumber" to customer.phoneNumber
                        )
                    onSuccess()
                } else {
                    onError("Customer not found.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while updating a Customer information: ${e.message}")
        }
    }

}