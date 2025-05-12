import SwiftUI

class MarkerInfoSheetViewModel: ObservableObject{
 
    func getSpotImage(id: Int8) -> URL {
        return Service().getSpotPicture(id: id)
    }
    
    func getAttributeImage(id: Int) -> URL {
        return Service().getAttributesImageUrl(id: id)
    }
    
    func getScreenWidth() -> CGFloat {
        return UIScreen.main.bounds.size.width - 20
    }
    
    func getScreenHeight() -> CGFloat {
        return UIScreen.main.bounds.size.height * 0.5
    }
}
