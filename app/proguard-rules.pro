# VaaniVerse4U release configuration
#
# Hilt, Room, Firebase and Compose ship focused consumer rules. Do not add broad
# package-wide keep rules for them: doing so would disable useful R8 shrinking.

# Gson parses the bundled JSON curriculum using reflection. Preserve only the
# model fields and small catalog DTOs whose JSON member names are the contract.
-keepattributes Signature,InnerClasses,EnclosingMethod
-keep,allowoptimization,allowobfuscation class com.google.gson.reflect.TypeToken
-keep,allowoptimization,allowobfuscation class * extends com.google.gson.reflect.TypeToken

-keep,allowoptimization class com.code4galaxy.vaaniverse4u.data.local.Catalog { <fields>; }
-keep,allowoptimization class com.code4galaxy.vaaniverse4u.data.local.LanguageCurriculum { <fields>; }
-keep,allowoptimization class com.code4galaxy.vaaniverse4u.data.local.CurriculumLesson { <fields>; }
-keep,allowoptimization class com.code4galaxy.vaaniverse4u.data.local.CurriculumWord { <fields>; }

-keep,allowoptimization class com.code4galaxy.vaaniverse4u.domain.model.Language { <fields>; }
-keep,allowoptimization class com.code4galaxy.vaaniverse4u.domain.model.LanguagePair { <fields>; }
-keep,allowoptimization class com.code4galaxy.vaaniverse4u.domain.model.LearnerProgress { <fields>; }
-keep,allowoptimization class com.code4galaxy.vaaniverse4u.domain.model.DailyChallenge { <fields>; }
-keep,allowoptimization class com.code4galaxy.vaaniverse4u.domain.model.AchievementDefinition { <fields>; }

# Keep source information in retrace mapping files for crash de-obfuscation.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
