import CoreLocation
import SwiftUI

struct Address {
    var street: String?
    var city: String?
    var country: String?
    var fullAddress: String {
        var components: [String] = []
        if let street = street { components.append(street) }
        if let city = city { components.append(city) }
        if let country = country { components.append(country) }
        return components.joined(separator: ", ")
    }
}

extension CLLocationCoordinate2D {
    func getAddress(completion: @escaping (Address?) -> Void) {
        let location = CLLocation(latitude: self.latitude, longitude: self.longitude)
        let geocoder = CLGeocoder()

        geocoder.reverseGeocodeLocation(location) { placemarks, error in
            if let error = error {
                print("Reverse geocoding failed: \(error.localizedDescription)")
                completion(nil)
                return
            }

            guard let placemark = placemarks?.first else {
                completion(nil)
                return
            }

            let address = Address(
                street: placemark.thoroughfare,
                city: placemark.locality,
                country: placemark.country
            )

            completion(address)
        }
    }
}
