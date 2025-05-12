struct MarkerDto: Codable, Hashable, Identifiable {
    var id: Int { // Definiere die `id`-Eigenschaft für `Identifiable`
           return spotId
       }
    
    let spotId: Int;
    let title: String
    var latitude: Double;
    var longitude: Double;
    let city: String;
    let sport: Sport;
}
