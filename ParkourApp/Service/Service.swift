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
    
    func register(username: String, email: String, password: String) -> AnyPublisher<User, Error> {
        let url = self.getFullUrl(subpath: "/authentication/register")
        var requestURL = URLRequest(url: url!)
        requestURL.httpMethod = "POST"
        let body : [String: Any] = [
            "username": username,
            "email": email,
            "password": password
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
        return getProfilePicture(username: user.username, colorScheme: colorScheme)
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
    
    func getSpotPicture(id: Int8) -> URL {
        return URL(string: url + "/spot/images/\(id)")!
    }
    
    func getFullUrl(subpath: String) -> URL! {
        return URL(string: url + subpath)!
    }
}

let url = "http://localhost:8080/api"
//192.168.0.19

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
