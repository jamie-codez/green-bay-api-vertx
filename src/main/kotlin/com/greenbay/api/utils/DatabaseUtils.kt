package com.greenbay.api.utils

import io.vertx.core.Vertx
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.AggregateOptions
import io.vertx.ext.mongo.BulkOperation
import io.vertx.ext.mongo.CountOptions
import io.vertx.ext.mongo.FindOptions
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.mongo.MongoClientBulkWriteResult

/**
 * The Database Utilities class for DB operations
 * @param vertx The vertx instance to be used
 * @author Jamie Omondi
 * @since 05-03-2023
 * @see Vertx
 */
class DatabaseUtils(private val vertx: Vertx) {
    /**
     * For logging purposes
     */
    private val logger = LoggerFactory.getLogger(this.javaClass.simpleName)

    /**
     * The db client that will be used to perform all the CRUD operations in and out of the DB
     */
    private lateinit var mongoClient: MongoClient

    init {
        mongoClient = MongoClient.createShared(vertx, getDBConfig())
    }

    /**
     * Provides the instance of mongo client created in this class
     * @author Jamie Omondi
     * @since 05-06-2023
     * @see MongoClient
     */

    fun getMongoClient() = this.mongoClient

    /**
     * Provides the database configurations to the client
     * @author Jamie Omondi
     * @since 05-06-2023
     */
    private fun getDBConfig() = JsonObject.of(
        "keepAlive", true,
        "socketTimeoutMS", 5_000,
        "connectTimeoutMS", 5_000,
        "maxIdleTimeMS", 90_000,
        "autoReconnect", true,
        "db_name", System.getenv("GB_DB_NAME"),
        "url", System.getenv("GB_DB_CON_STRING"),
        "username", System.getenv("GB_DB_USERNAME"),
        "password", System.getenv("GB_DB_PASSWORD"),
        "authSource", "admin"
    )

    /**
     * Inserts the parsed json document to the named collection
     * @param collection The collection to house the data
     * @param document the document to be saved
     * @param success Th callback function called when operation is successful
     * @param fail The callback function called when operation fails
     * @author Jamie Omondi
     * @since 05-06-2023
     */
    fun insert(
        collection: String,
        document: JsonObject,
        success: (docId: String) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("insert() -->")
        this.getMongoClient().insert(collection, document) {
            if (it.succeeded()) {
                success(it.result())
            } else {
                logger.error("insert(${it.cause().message}) <--")
                fail(it.cause())
            }
        }
        logger.info("insert() <--")
    }

    /**
     * Saves the parsed json document to the named collection -> Works same as insert
     * @param collection The collection to house the data
     * @param document the document to be saved
     * @param success Th callback function called when operation is successful
     * @param fail The callback function called when operation fails
     * @author Jamie Omondi
     * @since 05-06-2023
     */
    fun save(
        collection: String,
        document: JsonObject,
        success: (docId: String) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("insert() -->")
        this.getMongoClient().save(collection, document) {
            if (it.succeeded()) {
                success(it.result())
            } else {
                logger.error("insert(${it.cause().message}) <--")
                fail(it.cause())
            }
        }
        logger.info("insert() <--")
    }

    /**
     * Saves many documents in the parsed collection asynchronously
     * @param collection The collection to house the documents
     * @param bulkOperation The documents to be bulk written
     * @param success The callback function called when operation is successful
     * @param fail The callback function called when operation fails
     * @author Jamie Omondi
     * @since 05-06-2023
     */
    fun bulkSave(
        collection: String,
        bulkOperation: List<BulkOperation>,
        success: (result: MongoClientBulkWriteResult) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("bulkSave() -->")
        this.getMongoClient().bulkWrite(collection, bulkOperation) {
            if (it.succeeded()) {
                success(it.result())
            } else {
                logger.error("bulkSave(${it.cause().message}) <--")
                fail(it.cause())
            }
        }
        logger.info("bulkSave() <--")
    }


    /**
     * Creates an index in the parsed collection on the fields parsed
     * @param collection The collection housing the documents to be indexed
     * @param fields The fields to be indexed
     * @param success The callback function called when operation was successful
     * @param fail The callback function called when operation failed
     * @author Jamie Omondi
     * @since 05-06-2023
     */
    fun createIndex(
        collection: String,
        fields: JsonObject,
        success: (void: Void) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("createIndex() -->")
        this.getMongoClient().createIndex(collection, fields) {
            if (it.succeeded()) {
                success(it.result())
            } else {
                logger.error("createIndex(${it.cause().message}) <--")
                fail(it.cause())
            }
        }
        logger.info("createIndex() <--")
    }

    /**
     * Drops an index in the parsed collection
     * @param collection The collection housing the documents with the indexed
     * @param indexName The name of the index
     * @param success The callback function called when operation was successful
     * @param fail The callback function called when operation failed
     * @author Jamie Omondi
     * @since 05-06-2023
     */
    fun dropIndex(
        collection: String,
        indexName: String,
        success: (void: Void) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("dropIndex() -->")
        this.getMongoClient().dropIndex(collection, indexName) {
            if (it.succeeded()) {
                success(it.result())
            } else {
                logger.error("dropIndex(${it.cause().message}) <--")
                fail(it.cause())
            }
        }
        logger.info("dropIndex() <--")
    }

    /**
     * Gets one document from the collection that is distinct with the given query
     * @param collection The collection housing the document
     * @param query The query used to filter the data
     * @param fields The fields that you want to be returned in the document
     * @param success The callback function called when operation is successful
     * @param fail The callback function called when operation fails
     */
    fun findOne(
        collection: String,
        query: JsonObject,
        fields: JsonObject,
        success: (result: JsonObject) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("findOne() -->")
        this.getMongoClient().findOne(collection, query, fields) {
            if (it.succeeded()) {
                success(it.result())
            } else {
                logger.error("findOne(${it.cause().message}) <--")
                fail(it.cause())
            }
        }
        logger.info("findOne() <--")
    }

    /**
     * Fetches documents form the given collection filtering wit the parsed query
     * @param collection The collection housing the documents
     * @param query The query used to filter the data
     * @param success The callback function called when operation is successful
     * @param fail The callback function called when operation fails
     * @author Jamie Omondi
     * @since 05-06-2023
     */
    fun find(
        collection: String,
        query: JsonObject,
        success: (result: List<JsonObject>) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("find() -->")
        this.getMongoClient().find(collection, query) {
            if (it.succeeded()) {
                success(it.result())
            } else {
                logger.error("find(${it.cause().message}) <--")
                fail(it.cause())
            }
        }
        logger.info("find() <--")
    }

    /**
     * Gets documents from a collection with the given query and find options parsed
     * @param collection The collection housing the data
     * @param query The query use to filter the data
     * @param findOptions The findOptions to be used to query dat from the collection
     * @param success The callback function called when the operation is successful
     * @param fail The callback function called when the operation failed
     * @author Jamie Omondi
     * @since 05-06-2-23
     */
    fun findWithOptions(
        collection: String,
        query: JsonObject,
        findOptions: FindOptions,
        success: (result: List<JsonObject>) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("findWithOptions() -->")
        this.getMongoClient().findWithOptions(collection, query, findOptions) {
            if (it.succeeded()) {
                success(it.result())
            } else {
                logger.error("findWithOptions(${it.cause().message}) <--")
                fail(it.cause())
            }
        }
        logger.info("findWithOptions() <--")
    }

    /**
     * Use aggregation to fetch data
     * @param collection The collection housing the documents
     * @param pipeline The query pipeline
     * @param success The callback function called when operation is successful
     * @param fail The callback function called when operation failed
     * @author Jamie Omondi
     * @since 06-06-2023
     */
    fun aggregate(
        collection: String,
        pipeline: JsonArray,
        success: (result: List<JsonObject>) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("aggregate() -->")
        val data = ArrayList<JsonObject>()
        this.getMongoClient().aggregate(collection, pipeline)
            .handler {
                logger.info("aggregate(Streaming data...) <--")
                data.add(it)
            }
            .endHandler {
                logger.info("aggregate(Finished streaming data...) <--")
                success(data)
            }
            .exceptionHandler {
                logger.error("aggregate(${it.message}) <--")
                fail(it)
            }
        logger.info("aggregate() <--")
    }

    /**
     * Get the count of documents in a given collection with the parsed query
     * @param collection The collection housing the documents
     * @param query The query used for filtering the data
     * @param success the callback function called when result is successful
     * @param fail The callback function called when operation failed
     * @author Jamie Omondi
     * @since 06-06-2023
     */
    fun count(
        collection: String,
        query: JsonObject,
        success: (result:Long) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("count() -->")
        this.getMongoClient().count(collection, query){
            if (it.succeeded()){
                success(it.result())
            }else{
                fail(it.cause())
            }
        }
        logger.info("count() <--")
    }

    /**
     * Get the count of documents in a given collection with the parsed query
     * @param collection The collection housing the documents
     * @param query The query used for filtering the data
     * @param success the callback function called when result is successful
     * @param fail The callback function called when operation failed
     * @author Jamie Omondi
     * @since 06-06-2023
     */
    fun countWithOptions(
        collection: String,
        query: JsonObject,
        options:CountOptions,
        success: (result:Long) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("countWithOptions() -->")
        this.getMongoClient().countWithOptions(collection, query,options){
            if (it.succeeded()){
                success(it.result())
            }else{
                logger.error("countWithOptions(${it.cause().message}) <--")
                fail(it.cause())
            }
        }
        logger.info("countWithOptions() <--")
    }


    /**
     * Use aggregation to fetch data with the parsed options
     * @param collection The collection housing the documents
     * @param pipeline The query pipeline
     * @param success The callback function called when operation is successful
     * @param fail The callback function called when operation failed
     * @author Jamie Omondi
     * @since 06-06-2023
     */
    fun aggregateWithOptions(
        collection: String,
        pipeline: JsonArray,
        options: AggregateOptions,
        success: (result: List<JsonObject>) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("aggregateWithOptions() -->")
        val data = ArrayList<JsonObject>()
        this.getMongoClient().aggregateWithOptions(collection, pipeline, options)
            .handler {
                logger.info("aggregateWithOptions(Streaming data...) <--")
                data.add(it)
            }
            .endHandler {
                logger.info("aggregateWithOptions(Finished streaming data...) <--")
                success(data)
            }
            .exceptionHandler {
                logger.error("aggregateWithOptions(${it.message}) <--")
                fail(it)
            }
        logger.info("aggregateWithOptions() <--")
    }

    /**
     * Finds One document and updates in the parsed collection and updates the parsed fields
     * @param collection The collection housing the document to be updated
     * @param query The query used to filter and located the document
     * @param update The update payload
     * @param success The callback function called when operation is successful
     * @param fail The callback function called when operation is successful
     * @author Jamie Omondi
     * @since 05-06-2023
     */
    fun findOneAndUpdate(
        collection: String,
        query: JsonObject,
        update: JsonObject,
        success: (result: JsonObject) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("findOneAndUpdate() -->")
        this.getMongoClient().findOneAndUpdate(collection, query, update) {
            if (it.succeeded()) {
                success(it.result())
            } else {
                logger.error("findOneAndUpdate(${it.cause().message}) <--")
                fail(it.cause())
            }
        }
        logger.info("findOneAndUpdate() <--")
    }

    /**
     * Finds a document in the parsed collection with the query parsed and deletes the documents
     * @param collection The collection housing the document
     * @param query The query used to filter the data
     * @param success The callback function called when operation is successful
     * @param fail The callback function called when operation failed
     * @author Jamie Omondi
     * @since 06-06-2023
     */
    fun findOneAndDelete(
        collection: String,
        query: JsonObject,
        success: (result: JsonObject) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("findOneAndDelete() -->")
        this.getMongoClient().findOneAndDelete(collection, query) {
            if (it.succeeded()) {
                success(it.result())
            } else {
                logger.error("findOneAndDelete(${it.cause().message}) <--")
                fail(it.cause())
            }
        }
        logger.info("findOneAndDelete() <--")
    }

    /**
     * Finds a document in the parsed collection with the query parsed and deletes the documents-> works same as findOneAndDelete
     * @param collection The collection housing the document
     * @param query The query used to filter the data
     * @param success The callback function called when operation is successful
     * @param fail The callback function called when operation failed
     * @author Jamie Omondi
     * @since 06-06-2023
     */
    fun removeDocument(
        collection: String,
        query: JsonObject,
        success: (result: JsonObject) -> Unit,
        fail: (throwable: Throwable) -> Unit
    ) {
        logger.info("removeDocument() -->")
        this.getMongoClient().removeDocument(collection, query) {
            if (it.succeeded()) {
                success(it.result().toJson())
            } else {
                logger.error("removeDocument(${it.cause().message}) <--")
                fail(it.cause())
            }
        }
        logger.info("removeDocument() <--")
    }


}