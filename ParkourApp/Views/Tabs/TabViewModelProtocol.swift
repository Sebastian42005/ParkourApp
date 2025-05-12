protocol TabViewModelProtocol {
    var selectedSport: Sport? { get set }
    var selectedAttributes: [Attribute] { get set }
    var selectedCity: String? { get set }
    var selectedRating: Int? { get set }
    var showFilterSheet: Bool { get set }
}
