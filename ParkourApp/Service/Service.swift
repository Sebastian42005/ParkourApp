import Foundation
import Combine
import SwiftUI

class Service {
    private func addAuthorizationHeader(to request: inout URLRequest) {
            if let token = UserDefaults.standard.string(forKey: "token") {
                request.addValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
            }
        }
    
    func login(username: String, password: String) -> AnyPublisher<Token, Error> {
        let url = self.getFullUrl(subpath: "/authentication/login")
        var requestURL = URLRequest(url: url!)
        requestURL.httpMethod = "POST"
        let body : [String: Any] = [
            "username": username,
            "password": password
        ]
        requestURL.addValue("application/json", forHTTPHeaderField: "content-type")
        requestURL.httpBody = try! JSONSerialization.data(withJSONObject: body)
        return URLSession.shared.dataTaskPublisher(for: requestURL)
            .mapError { $0 as Error}
            .map { $0.data }
            .decode(type: Token.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func register(username: String, email: String, password: String, favoriteSports: [String]) -> AnyPublisher<User, Error> {
        let url = self.getFullUrl(subpath: "/authentication/register")
        var requestURL = URLRequest(url: url!)
        requestURL.httpMethod = "POST"
        let body : [String: Any] = [
            "username": username,
            "email": email,
            "password": password,
            "favoriteSports": favoriteSports
        ]
        requestURL.addValue("application/json", forHTTPHeaderField: "content-type")
        requestURL.httpBody = try! JSONSerialization.data(withJSONObject: body)
        return URLSession.shared.dataTaskPublisher(for: requestURL)
            .mapError { $0 as Error}
            .map { $0.data }
            .decode(type: User.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func checkLogin(username: String) -> AnyPublisher<LoginStateDto, Error> {
        let url = self.getFullUrl(subpath: "/authentication/check-login/" + username)
        var requestURL = URLRequest(url: url!)
        requestURL.httpMethod = "GET"
        
        return URLSession.shared.dataTaskPublisher(for: requestURL)
            .mapError { $0 as Error }
            .map { $0.data }
            .decode(type: LoginStateDto.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func verifyToken() -> AnyPublisher<Verify, Error> {
        let url = self.getFullUrl(subpath: "/authentication/verify-token")
        
        return URLSession.shared.dataTaskPublisher(for: url!)
            .map{ $0.data }
            .mapError { $0 as Error }
            .decode(type: Verify.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func getOwnUser() -> AnyPublisher<User, Error> {
        let url = self.getFullUrl(subpath: "/user/own")
        var requestURL = URLRequest(url: url!)
        
        addAuthorizationHeader(to: &requestURL)

        return URLSession.shared.dataTaskPublisher(for: requestURL)
            .map { $0.data }
            .mapError { $0 as Error }
            .decode(type: User.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func getProfilePicture(username: String, colorScheme: ColorScheme) -> URL {
        var url = URL(string: url + "/user/" + username + "/profile")!
        url.append(queryItems: [URLQueryItem(name: "dark", value: String(colorScheme == .dark))])
        return url
    }
    
    func getOwnProfilePicture(colorScheme: ColorScheme) -> URL {
        return getProfilePicture(username: OWN_USER.username, colorScheme: colorScheme)
    }
    
    func getAllSpots(search: String, spotType: String) -> AnyPublisher<SpotList, Error> {
        var url = self.getFullUrl(subpath: "/spot/all")
        url?.append(queryItems: [URLQueryItem(name: "search", value: search), URLQueryItem(name: "spotType", value: spotType)])
        
        var requestURL = URLRequest(url: url!)
        requestURL.httpMethod = "GET"
        return URLSession.shared.dataTaskPublisher(for: requestURL)
            .mapError { $0 as Error }
            .map { $0.data }
            .decode(type: SpotList.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func getUserSpots(username: String) -> AnyPublisher<SpotList, Error> {
        let url = self.getFullUrl(subpath: "/user/\(username)/spots")
        var requestURL = URLRequest(url: url!)
        requestURL.httpMethod = "GET"
        return URLSession.shared.dataTaskPublisher(for: requestURL)
            .mapError { $0 as Error }
            .map { $0.data }
            .decode(type: SpotList.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func getUser(username: String) -> AnyPublisher<User, Error> {
        let url = self.getFullUrl(subpath: "/user/\(username)")
        var requestURL = URLRequest(url: url!)
        requestURL.httpMethod = "GET"
        return URLSession.shared.dataTaskPublisher(for: requestURL)
            .mapError { $0 as Error }
            .map { $0.data }
            .decode(type: User.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func getSpot(id: Int) -> AnyPublisher<Spot, Error> {
        let url = self.getFullUrl(subpath: "/spot/\(id)")
        var requestURL = URLRequest(url: url!)
        requestURL.httpMethod = "GET"
        return URLSession.shared.dataTaskPublisher(for: requestURL)
            .mapError { $0 as Error }
            .map { $0.data }
            .decode(type: Spot.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func getSpotRatings(id: Int) -> AnyPublisher<[RatingDto], Error> {
        let url = self.getFullUrl(subpath: "/spot/\(id)/ratings")
        var requestURL = URLRequest(url: url!)
        requestURL.httpMethod = "GET"
        return URLSession.shared.dataTaskPublisher(for: requestURL)
            .mapError { $0 as Error }
            .map { $0.data }
            .decode(type: [RatingDto].self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }

    func uploadSpot(spot: SpotRequest) -> AnyPublisher<Spot, Error> {
        let url = self.getFullUrl(subpath: "/spot/post")
        var requestURL = URLRequest(url: url!)
        requestURL.httpMethod = "POST"
        
        do {
            let jsonData = try JSONEncoder().encode(spot)
            requestURL.httpBody = jsonData
        } catch {
            return Fail(error: error).eraseToAnyPublisher()
        }
        
        requestURL.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        addAuthorizationHeader(to: &requestURL)
        return URLSession.shared.dataTaskPublisher(for: requestURL)
            .mapError { $0 as Error }
            .map { $0.data }
            .decode(type: Spot.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }

    func uploadSpotImages(spotId: Int, images: [UIImage]) -> AnyPublisher<Void, Error> {
        let url = self.getFullUrl(subpath: "/spot/\(spotId)/images")!
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        
        let boundary = "Boundary-\(UUID().uuidString)"
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        addAuthorizationHeader(to: &request)

        var body = Data()

        for (index, image) in images.enumerated() {
            guard let imageData = image.jpegData(compressionQuality: 0.8) else { continue }
            let filename = "image\(index).jpg"
            
            body.append("--\(boundary)\r\n".data(using: .utf8)!)
            body.append("Content-Disposition: form-data; name=\"images\"; filename=\"\(filename)\"\r\n".data(using: .utf8)!)
            body.append("Content-Type: image/jpeg\r\n\r\n".data(using: .utf8)!)
            body.append(imageData)
            body.append("\r\n".data(using: .utf8)!)
        }

        body.append("--\(boundary)--\r\n".data(using: .utf8)!)
        request.httpBody = body

        return URLSession.shared.dataTaskPublisher(for: request)
            .mapError { $0 as Error }
            .map { _ in }
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }

    func uploadFeedback(feedback: FeedbackRequest) -> AnyPublisher<FeedbackResponse, Error> {
        let url = self.getFullUrl(subpath: "/feedback")
        var requestURL = URLRequest(url: url!)
        requestURL.httpMethod = "POST"
        
        do {
            let jsonData = try JSONEncoder().encode(feedback)
            requestURL.httpBody = jsonData
        } catch {
            return Fail(error: error).eraseToAnyPublisher()
        }
        
        requestURL.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        addAuthorizationHeader(to: &requestURL)
        return URLSession.shared.dataTaskPublisher(for: requestURL)
            .mapError { $0 as Error }
            .map { $0.data }
            .decode(type: FeedbackResponse.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func getSports() -> AnyPublisher<[Sport], Error> {
        let url = self.getFullUrl(subpath: "/sports")
        var requestURL = URLRequest(url: url!)
        requestURL.httpMethod = "GET"
        return URLSession.shared.dataTaskPublisher(for: requestURL)
            .mapError { $0 as Error }
            .map { $0.data }
            .decode(type: [Sport].self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func getSportImageUrl (name: String) -> URL {
        return URL(string: url + "/sports/" + name)!
    }
    
    func getAttributesImageUrl (id: Int) -> URL {
        return URL(string: url + "/attributes/" + String(id))!
    }
    
    func getMarkers(sport: Sport?, attributes: [Attribute]?, rating: Int?, search: String?, city: String?) -> AnyPublisher<[MarkerDto], Error> {
        var components = URLComponents(string: self.getFullUrl(subpath: "/spot/markers").absoluteString)

        var queryItems: [URLQueryItem] = []

        if let sport = sport {
            queryItems.append(URLQueryItem(name: "sport", value: "\(sport.name)"))
        }

        if let attributes = attributes, !attributes.isEmpty {
            let attributeIds = attributes.map { String($0.id) }
            queryItems.append(URLQueryItem(name: "attributes", value: attributeIds.joined(separator: ",")))
        }
        
        if let search = search?.trimmingCharacters(in: .whitespaces), !search.isEmpty {
            queryItems.append(URLQueryItem(name: "search", value: search))
        }
        
        if let city = city?.trimmingCharacters(in: .whitespaces), !city.isEmpty {
            queryItems.append(URLQueryItem(name: "city", value: city))
        }

        if let rating = rating {
            queryItems.append(URLQueryItem(name: "minRating", value: "\(rating)"))
        }

        components?.queryItems = queryItems

        guard let finalURL = components?.url else {
            return Fail(error: URLError(.badURL)).eraseToAnyPublisher()
        }

        var request = URLRequest(url: finalURL)
        request.httpMethod = "GET"

        return URLSession.shared.dataTaskPublisher(for: request)
            .mapError { $0 as Error }
            .map { $0.data }
            .decode(type: [MarkerDto].self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func searchSpots(sport: Sport?, attributes: [Attribute]?, rating: Int?, search: String?, city: String?) -> AnyPublisher<SpotList, Error> {
        var components = URLComponents(string: self.getFullUrl(subpath: "/spot/all").absoluteString)

        var queryItems: [URLQueryItem] = []

        if let sport = sport {
            queryItems.append(URLQueryItem(name: "sport", value: "\(sport.name)"))
        }

        if let attributes = attributes, !attributes.isEmpty {
            let attributeIds = attributes.map { String($0.id) }
            queryItems.append(URLQueryItem(name: "attributes", value: attributeIds.joined(separator: ",")))
        }
        
        if let search = search?.trimmingCharacters(in: .whitespaces), !search.isEmpty {
            queryItems.append(URLQueryItem(name: "search", value: search))
        }
        
        if let city = city?.trimmingCharacters(in: .whitespaces), !city.isEmpty {
            queryItems.append(URLQueryItem(name: "city", value: city))
        }

        if let rating = rating {
            queryItems.append(URLQueryItem(name: "minRating", value: "\(rating)"))
        }

        components?.queryItems = queryItems

        guard let finalURL = components?.url else {
            return Fail(error: URLError(.badURL)).eraseToAnyPublisher()
        }

        var request = URLRequest(url: finalURL)
        request.httpMethod = "GET"

        return URLSession.shared.dataTaskPublisher(for: request)
            .mapError { $0 as Error }
            .map { $0.data }
            .decode(type: SpotList.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func searchUsers(search: String?) -> AnyPublisher<[String], Error> {
        var components = URLComponents(string: self.getFullUrl(subpath: "/user/search").absoluteString)

        var queryItems: [URLQueryItem] = []

        if let search = search?.trimmingCharacters(in: .whitespaces), !search.isEmpty {
            queryItems.append(URLQueryItem(name: "search", value: search))
        }

        components?.queryItems = queryItems

        guard let finalURL = components?.url else {
            return Fail(error: URLError(.badURL)).eraseToAnyPublisher()
        }

        var request = URLRequest(url: finalURL)
        request.httpMethod = "GET"

        return URLSession.shared.dataTaskPublisher(for: request)
            .mapError { $0 as Error }
            .map { $0.data }
            .decode(type: [String].self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }

    func getAllCities() -> AnyPublisher<[String], Error> {
        let url = self.getFullUrl(subpath: "/locations/cities")
        var requestURL = URLRequest(url: url!)
        requestURL.httpMethod = "GET"
        return URLSession.shared.dataTaskPublisher(for: requestURL)
            .mapError { $0 as Error }
            .map { $0.data }
            .decode(type: [String].self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func getSpotPicture(id: Int8) -> URL {
        return URL(string: url + "/spot/images/\(id)")!
    }
    
    func getLowResSpotPicture(id: Int8) -> URL {
        return URL(string: url + "/spot/images/\(id)/low-res")!
    }
    
    func getFullUrl(subpath: String) -> URL! {
        return URL(string: url + subpath)!
    }
    
    func rateSpot(spotId: Int, stars: Int, description: String) -> AnyPublisher<RatingDto, Error> {
        let url = self.getFullUrl(subpath: "/spot/\(spotId)/rate")
        var requestURL = URLRequest(url: url!)
        requestURL.httpMethod = "PUT"
        let rating = RatingRequest(stars: stars, message: description)
        do {
            let jsonData = try JSONEncoder().encode(rating)
            requestURL.httpBody = jsonData
        } catch {
            return Fail(error: error).eraseToAnyPublisher()
        }
        
        requestURL.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        addAuthorizationHeader(to: &requestURL)
        return URLSession.shared.dataTaskPublisher(for: requestURL)
            .mapError { $0 as Error }
            .map { $0.data }
            .decode(type: RatingDto.self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
}

let url = "http://192.168.0.141:8080/api"
//192.168.0.141

extension Data {
    var bytes: [UInt8] {
        return [UInt8](self)
    }
}

struct Token: Codable, Hashable {
    let token: String?
    let user: User?
}

struct LoginStateDto: Codable, Hashable {
    let loggedIn: Bool
}

struct LoginCredentials: Codable, Hashable {
    let username: String
    let password: String
}

enum Role: String {
    case guest = "GUEST"
    case user = "USER"
    case company = "COMPANY"
    case admin = "ADMIN"
}

struct Verify: Codable, Hashable {
    let verified: Bool
    let user: User?
}

extension Data {

    var mimeType: String? {
        var values = [UInt8](repeating: 0, count: 1)
        copyBytes(to: &values, count: 1)

        switch values[0] {
        case 0xFF:
            return "image/jpeg"
        case 0x89:
            return "image/png"
        case 0x47:
            return "image/gif"
        case 0x49, 0x4D:
            return "image/tiff"
        default:
            return nil
        }
    }
}
