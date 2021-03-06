# MACM Android SDK

The Android SDK is an Android API to retrieve content from a MACM server.

### Table Of Contents

1. [Installation](#installation)
    1. [SDK](#sdk)
    2. [Sample app](#sample-app)
2. [Getting started](#getting-started)
    1. [Authentication](#authentication)
        1. [Authentication with the credentials of the application](#authentication-with-the-credentials-of-the-application)
        2. [Authentication with the credentials of the end user](#authentication-with-the-credentials-of-the-end-user)
    2. [Retrieving Content](#retrieving-content)
        1. [List of content items by path](#querying-a-list-of-content-items-by-path)
        2. [List of content items by id](#querying-a-list-of-content-items-by-id)
        3. [Single content item by id](#querying-a-single-content-item-by-id)
        4. [Single content item by path](#querying-a-single-content-item-by-path)
    3. [Querying an asset (image) by its url](#querying-an-asset-image-by-its-url)
    4. [Project-related queries](#project-related-queries)
        1. [View all open projects](#view-all-open-projects)
        2. [Accessing draft content in project scope](#accessing-draft-content-in-project-scope)

3. [Miscellaneous](#miscellaneous)
    1. [Allowing untrusted certificates for HTTPS connections](#allowing-untrusted-certificates-with-https-connections)
    2. [Enabling geo-location retrieval](#enabling-geo-location-retrieval)
4. [Documentation references](#documentation-references)

## Installation
### SDK

The SDK may be installed by referencing the **MACM-SDK-android-xxx.aar** android archive found in this repository's *dist/* folder.
For instance, this can be done as follows in your build.gradle script, assuming the .aar file was copied in the app's 'libs/' folder:

```groovy
repositories {
  flatDir {
    dirs 'libs'
  }
}

dependencies {
  compile(name: 'MACM-SDK-android-release', ext: 'aar')
}
```

### Sample app

To install and run the sample app 'AndroidSDKTestApp':
* clone this Git repository
* import it in Android Studio
* the app can now be built and run from Android Studio as any other project


## Getting started
### Authentication

Authentication with the remote server can be done in two ways:
#### Authentication with the credentials of the application

The username and password are hardcoded in the application and the following constructor should be used:

```java
// create a service instance with application credentials
CAASService service = new CAASService("https://macm-rendering.saas.ibmcloud.com", "wps", "vp1234", "username", "password");
```

#### Authentication with the credentials of the end user

* The users must sign in against a MACM server with their own credentials, the following constructor must be used:

```java
// create a service instance without credentials
CAASService service = new CAASService("https://macm-rendering.saas.ibmcloud.com", "wps", "vp1234");
```

* When the users provide their credentials, the following API checks these credentials against the MACM server:

```java
// handle the sign-in result asynchronously
CAASDataCallback<Void> callback = new CAASDataCallback<Void>() {
  @Override
  public void onSuccess(CAASRequestResult<Void> requestResult) {
    // sign-in successful
  }

  @Override
  public void onError(CAASErrorResult error) {
    // handle the error
  }
};
// perform the explicit sign-in with user credentials
service.signIn("username", "password", callback);
```

### Retrieving content
#### Querying a list of content items by path

```java
// create the service that connects to the MACM instance
CAASService service = new CAASService("https://macm-rendering.saas.ibmcloud.com", "wps", "vp1234", "username", "password");
// handle the request result asynchronously
CAASDataCallback<CAASContentItemsList> callback = new CAASDataCallback<CAASContentItemsList>() {
  @Override
  public void onSuccess(CAASRequestResult<CAASContentItemsList> requestResult) {
    CAASContentItemsList itemsList = requestResult.getResult();
    List<CAASContentItem> items = itemsList.getContentItems();
    // do something with the list of items
  }

  @Override
  public void onError(CAASErrorResult error) {
    // handle the error
  }
};
// create the request
CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
request.setPath("Samples/Content Types/Book"); // path to "Book" content items
// include the specified properties in the response
request.addProperties(CAASProperties.OIS, CAASProperties.CONTENT_TYPE, CAASProperties.TITLE,
  CAASProperties.LAST_MODIFIED_DATE, CAASProperties.CATEGORIES, CAASProperties.KEYWORDS);
// request the url to the cover image
request.addElements("cover");
// request the first 10 items
request.setPageSize(10);
request.setPageNumber(1);
// execute the request
service.executeRequest(request);
```

#### Querying a list of content items by id

```java
// creates the service that connects to the MACM instance
CAASService service = new CAASService("https://macm-rendering.saas.ibmcloud.com", "wps", "vp1234", "username", "password");
// handle the request result asynchronously
CAASDataCallback<CAASContentItemsList> callback = ...;
// create the request
CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
// id of the content items list to retrieve
request.setOid("5f2c3fee-3994-45d4-9570-a6aa67ff2250");
// sort by descending status then ascending title
request.addSortDescriptor("status", false);
request.addSortDescriptor("title", true);
// only include the published items that have any of the specified keywords
request.setWorkflowStatus(CAASContentItemsRequest.WorkflowStatus.Published);
request.addAnyKeywords("bestseller", "top10");
// execute the request
service.executeRequest(request);
```

#### Querying a single content item by id

```java
// creates the service that connects to the MACM instance
CAASService service = new CAASService("https://macm-rendering.saas.ibmcloud.com", "wps", "vp1234", "username", "password");
// handle the request result asynchronously
CAASDataCallback<CAASContentItem> callback = new CAASDataCallback<CAASContentItem>() {
  @Override
  public void onSuccess(CAASRequestResult<CAASContentItem> requestResult) {
    CAASContentItem item = requestResult.getResult();
    // do something with the item
  }

  @Override
  public void onError(CAASErrorResult error) {
    // handle the error
  }
};

// create the request
CAASContentItemRequest request = new CAASContentItemRequest(callback);
// id of the content item to retrieve
request.setOid("5f2c3fee-3994-45d4-9570-a6aa67ff2250");
// execute the request
service.executeRequest(request);
```

#### Querying a single content item by path

```java
// create the service that connects to the MACM instance
CAASService service = new CAASService("https://macm-rendering.saas.ibmcloud.com", "wps", "vp1234", "username", "password");
CAASDataCallback<CAASContentItem> callback = ...;
// create the request
CAASContentItemRequest request = new CAASContentItemRequest(callback);
request.setPath("Samples/some/content item path");
// execute the request
service.executeRequest(request);
```

### Querying an asset (image) by its url

```java
// create the service that connects to the MACM instance
CAASService service = ...;
final Context context = ...;
final ImageView imageView = ...;
// url retrieved from a previous request
// this is a partial URL, the CAASService instance will infer the full URL
final String url = "/wps/wcm/myconnect/80686c98-9264-4391-bc00-3a039f4dc0b3/cover.jpg?MOD=AJPERES"
// callback that retrieves the image and sets it on a view
CAASDataCallback<byte[]> callback = new CAASDataCallback<byte[]>() {
  @Override
  public void onSuccess(CAASRequestResult<byte[]> requestResult) {
    // get the Content-Type response header
    String contentType = requestResult.getHttpURLConnection().getHeaderField("Content-Type");
    // if we have an image content type, build a Bitmap object from the raw data
    if ((contentType != null) && contentType.startsWith("image")) {
      Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
      BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
      imageView.setImageDrawable(drawable);
    }
  }

  @Override
  public void onError(CAASErrorResult error) {
    Log.e(LOG_TAG, "failed to load image at " + url + ", with error: " + error);
  }
};
CAASAssetRequest request = new CAASAssetRequest(url, callback);
service.executeRequest(request);
```

## Project-related queries
### View all open projects

```java
CAASService service = ...;
// handle the request result asynchronously
CAASDataCallback<CAASContentItemsList> callback = new CAASDataCallback<CAASContentItemsList>() {
  @Override
  public void onSuccess(CAASRequestResult<CAASContentItemsList> requestResult) {
    CAASContentItemsList projectsList = requestResult.getResult();
    List<CAASContentItem> projects = projectsList.getContentItems();
    // do something with the list of projects
  }

  @Override
  public void onError(CAASErrorResult error) {
    // handle the error
  }
};
// create the request
CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
// open projects are always in the "MACM System" library
request.setPath("MACM System/Views/Open Projects");
// include the specified properties in the response
request.addProperties(CAASProperties.UUID, CAASProperties.ITEM_COUNT, CAASProperties.TITLE, CAASProperties.LAST_MODIFIED_DATE);
service.executeRequest(request);
```

### Accessing draft content in project scope

```java
CAASService service = ...;
CAASDataCallback<CAASContentItemsList> callback = ...;
CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
request.setPath("Samples/Views/All");
// set the project scope
request.setProject("MyProject");
service.executeRequest(request);
```

## Miscellaneous
### Allowing untrusted certificates with HTTPS connections

By default, Android only allows trusted certificates whose certificate authority is in its trusted list.
To disable this behavior, for instance to test with self-signed certificates, the `allowUntrustedCertificates`
flag must be set to `true', as follows:

```java
// create the service that connects to the MACM instance
CAASService service = new CAASService("https://macm-rendering.saas.ibmcloud.com", "wps", "vp1234", "username", "password");
service.setAllowUntrustedCertificates(true);
...
```

### Enabling geo-location retrieval

For the Android SDK to access the available location providers, the application must provide an Android application [Context](http://developer.android.com/reference/android/content/Context.html).
This can be done as in the following example:

```java
public class MyActivity extends Activity {
  ...

  public void doLogin() {
    CAASService service = new CAASService("https://macm-rendering.saas.ibmcloud.com", "wps", "vp1234", "username", "password");
    service.setAndroidContext(getApplicationContext());
    ...;
  }
}
```

## Documentation references

* [MACM Android SDK Javadoc](https://digexp.github.io/MACM-SDK-android)
* [IBM Mobile Application Content Manager Knowledge Center](http://www-01.ibm.com/support/knowledgecenter/SSYK7J_8.5.0/macm/macm_rm.dita)
