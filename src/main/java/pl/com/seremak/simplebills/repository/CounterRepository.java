//package pl.com.seremak.simplebills.repository;
//
//import com.mongodb.client.model.Filters;
//import com.mongodb.reactivestreams.client.MongoClient;
//import com.mongodb.reactivestreams.client.MongoCollection;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//import org.bson.Document;
//import reactor.core.publisher.Mono;
//
//
//@Repository
//@RequiredArgsConstructor
//public class CounterRepository {
//
//    private static final String DatabaseName = "EXAMPLE";
//    private static final String CollectionName = "example";
//
//    private final MongoClient client;
//
//
//    public String getSequentialId() {
//        Mono.from(getCollection().find(Filters.eq("id")))
//    }
//
////    public List<String> allDocuments() {
////        final List<String> list = new ArrayList<>();
////        final MongoCollection<Document> data = client.getDatabase(DatabaseName).getCollection(CollectionName);
////        data.find().map(Document::toJson).forEach(list::add);
////        return list;
////    }
//
//
//    private MongoCollection<Document> getCollection() {
//        return client.getDatabase(DatabaseName)
//                .getCollection(CollectionName);
//    }
//}
